package website.magyar.adoration.web.provider;

import website.magyar.adoration.database.business.BusinessWithLink;
import website.magyar.adoration.database.business.BusinessWithPerson;
import website.magyar.adoration.database.business.BusinessWithSocial;
import website.magyar.adoration.database.business.helper.enums.AdorationMethodTypes;
import website.magyar.adoration.database.business.helper.enums.AdoratorStatusTypes;
import website.magyar.adoration.database.business.helper.enums.SocialStatusTypes;
import website.magyar.adoration.database.tables.Link;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.GuestInformationJson;
import website.magyar.adoration.web.json.InformationJson;
import website.magyar.adoration.web.json.PersonJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class to provide Information about users.
 */
@Component
public class InformationProvider {

    private final Logger logger = LoggerFactory.getLogger(InformationProvider.class);

    @Autowired
    private BusinessWithPerson businessWithPerson;
    @Autowired
    private BusinessWithSocial businessWithSocial;
    @Autowired
    private BusinessWithLink businessWithLink;
    @Autowired
    private CoordinatorProvider coordinatorProvider;

    /**
     * Get overall information about a registered adorator.
     *
     * @return with the info in json object form
     */
    public Object getInformation(CurrentUserInformationJson currentUserInformationJson) {
        var informationJson = new InformationJson();
        //get name and status
        var personId = currentUserInformationJson.personId;
        var person = businessWithPerson.getPersonById(personId);
        if (person == null) {
            //wow, we should not be here
            logger.warn("User got access to prohibited area: {}", currentUserInformationJson.loggedInUserName);
            informationJson.error = "access denied";
        } else {
            //we have person info
            informationJson.name = person.getName();
            informationJson.status = AdoratorStatusTypes.getTranslatedString(person.getAdorationStatus());
            informationJson.id = person.getId().toString();
            informationJson.linkList = businessWithLink.getLinksOfPerson(person);
            informationJson.hoursCancelled = new HashSet<>();
            for (Link link: informationJson.linkList) {
                if (link.getType().equals(AdorationMethodTypes.ONETIME_OFF.getAdorationMethodValue())) {
                    informationJson.hoursCancelled.add(link.getHourId());
                }
            }
            informationJson.leadership = coordinatorProvider.getLeadership(currentUserInformationJson);
            var cal = Calendar.getInstance();
            int hourId = (cal.get(Calendar.DAY_OF_WEEK) - 1) * BusinessWithLink.HOUR_IN_A_DAY + cal.get(Calendar.HOUR_OF_DAY);  // use sun as 0 day
            informationJson.hourInDayNow = hourId % BusinessWithLink.HOUR_IN_A_DAY;
            informationJson.hourInDayNext = (hourId + 1) % BusinessWithLink.HOUR_IN_A_DAY;
            informationJson.currentHourList = businessWithLink.getLinksOfHour(hourId);
            informationJson.futureHourList = businessWithLink.getLinksOfHour((hourId + 1) % (Link.MAX_HOUR + 1));
            fillRelatedPersonIds(informationJson, currentUserInformationJson.isPrivilegedUser());
            //fill the day names first
            informationJson.dayNames = currentUserInformationJson.getUserDayNames();
        }
        return informationJson;
    }

    private void fillRelatedPersonIds(InformationJson informationJson, boolean isPrivilegedUser) {
        //NOTE: linkList, currentHourList and futureHourList must be filled already
        Set<Long> personIds = new HashSet<>();
        if (informationJson.linkList != null) {
            for (var l : informationJson.linkList) {
                personIds.add(l.getPersonId());
            }
        }
        if (informationJson.currentHourList != null) {
            for (var l : informationJson.currentHourList) {
                personIds.add(l.getPersonId());
            }
        }
        if (informationJson.futureHourList != null) {
            for (var l : informationJson.futureHourList) {
                personIds.add(l.getPersonId());
            }
        }
        var ppl = personIds.iterator();
        List<PersonJson> relatedPersonList = new LinkedList<>();
        while (ppl.hasNext()) {
            Long id = ppl.next();
            var p = businessWithPerson.getPersonById(id);
            if (p != null) {
                relatedPersonList.add(new PersonJson(p, isPrivilegedUser));
            } else {
                logger.warn("Person ID usage found without real Person, id: {}", id);
            }
        }
        informationJson.relatedPersonList = relatedPersonList;
    }

    /**
     * Get information for a guest.
     *
     * @return with the info in json object form
     */
    public Object getGuestInformation(CurrentUserInformationJson currentUserInformationJson) {
        var guestInformationJson = new GuestInformationJson();
        //get name and status
        var socialId = currentUserInformationJson.socialId;
        var social = businessWithSocial.getSocialById(socialId);
        if (social == null) {
            //wow, we should not be here
            logger.warn("Guest User got access to prohibited area: {}", currentUserInformationJson.loggedInUserName);
            guestInformationJson.error = "access denied";
        } else {
            //we have social info
            if (social.getFacebookUserId().length() > 0) {
                guestInformationJson.isFacebook = true;
                guestInformationJson.emailFacebook = social.getFacebookEmail();
                guestInformationJson.nameFacebook = social.getFacebookUserName();
            } else {
                guestInformationJson.isFacebook = false;
            }
            if (social.getGoogleUserId().length() > 0) {
                guestInformationJson.isGoogle = true;
                guestInformationJson.emailGoogle = social.getGoogleEmail();
                guestInformationJson.nameGoogle = social.getGoogleUserName();
            } else {
                guestInformationJson.isGoogle = false;
            }
            switch (SocialStatusTypes.getTypeFromId(social.getSocialStatus())) {
            case IDENTIFIED_USER: //adoráló - we should not be here
                guestInformationJson.status = currentUserInformationJson.getLanguageString("getInformation.statusRegistered");
                break;
            case SOCIAL_USER: //guest
                guestInformationJson.status = currentUserInformationJson.getLanguageString("getInformation.statusGuest");
                break;
            default:
            case WAIT_FOR_IDENTIFICATION: //waiting for identification
                guestInformationJson.status = currentUserInformationJson.getLanguageString("getInformation.statusUnknown");
                break;
            }
            guestInformationJson.leadership = coordinatorProvider.getLeadership(currentUserInformationJson);
            guestInformationJson.socialServiceUsed = currentUserInformationJson.socialServiceUsed;
        }
        return guestInformationJson;
    }
}
