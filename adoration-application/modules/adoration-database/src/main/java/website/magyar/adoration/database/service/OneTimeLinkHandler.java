package website.magyar.adoration.database.service;

import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.business.BusinessWithLink;
import website.magyar.adoration.database.business.helper.DateTimeConverter;
import website.magyar.adoration.database.business.helper.enums.AdorationMethodTypes;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

/**
 * Class to handle database clean-up of One-Time adorations.
 */
@Component
public class OneTimeLinkHandler {
    private static final long TIMER_DELAY = 15000;
    private static final long TIMER_PERIOD = 900000;
    private static Timer oneTimeLinkTimer;

    @Autowired
    BusinessWithLink businessWithLink;
    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;

    /**
     * Method to initialize the service timer.
     *
     */
    public OneTimeLinkHandler() {
        if (oneTimeLinkTimer == null) { //initiate timer
            oneTimeLinkTimer = new Timer(true);
            oneTimeLinkTimer.scheduleAtFixedRate(new OneTimeLinkHandlerTimerTask(this), TIMER_DELAY, TIMER_PERIOD);
        }
    }

    /**
     * Collect obsolete one-time links and remove them.
     * A link is obsolete if:
     *  has type: One-Time-On/Off AND
     *  EITHER has no meaningful admin comment which is: YYYY-MM-DD
     *  OR admin comment is correct, but in the past
     */
    public void timerTick() {
        Set<Link> linkSet = new HashSet<>();
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        Date todayLimit = dateTimeConverter.getDateNDaysAgo(1);
        List<Link> linkList = businessWithLink.getLinkList();
        for (Link link: linkList) {
            if (link.getType().equals(AdorationMethodTypes.ONETIME_ON.getAdorationMethodValue())
                    || link.getType().equals(AdorationMethodTypes.ONETIME_OFF.getAdorationMethodValue())) {
                try {
                    Date targetDate = dateTimeConverter.getDate(link.getAdminComment());
                    if (todayLimit.after(targetDate)) {
                        linkSet.add(link);
                    }
                } catch (ParseException e) {
                    //invalid admin text so must be cleaned up
                    linkSet.add(link);
                }
            }
        }
        //links to be deleted are collected in the set
        for (Link link: linkSet) {
            AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(link.getPersonId(), "System OneTimeLink Timer",
                    "Link:Delete:" + link.getId().toString(), "Day/Hour: " + businessWithLink.getDayNameFromHourId(link.getHourId())
                            + "/" + businessWithLink.getHourFromHourId(link.getHourId()),
                    "Type: " + AdorationMethodTypes.getTranslatedString(link.getType())
                            + ", Priority: " + link.getPriority().toString());
            businessWithLink.deleteLink(link, auditTrail);
        }
    }

}
