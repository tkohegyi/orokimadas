package website.magyar.adoration.database.business;

import com.sun.istack.NotNull; //NOSONAR
import org.hibernate.Session;
import org.hibernate.query.Query;
import website.magyar.adoration.database.SessionFactoryHelper;
import website.magyar.adoration.database.business.helper.BusinessBase;
import website.magyar.adoration.database.business.helper.enums.TranslatorDayNames;
import website.magyar.adoration.database.exception.DatabaseHandlingException;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Link;
import website.magyar.adoration.database.tables.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Business class to handle Link Database table.
 * This table holds information about hours assigned/covered by a person.
 */
@Component
public class BusinessWithLink extends BusinessBase {
    public static final Integer HOUR_IN_A_DAY = 24;
    public static final Integer MIN_DAY = 0;
    public static final Integer MAX_DAY = 6;
    public static final Integer PRIORITY_BORDER = 3; //below this is high priority

    private final Logger logger = LoggerFactory.getLogger(BusinessWithLink.class);

    /**
     * Check if the given hour is valid. Valid if less than hours a week has.
     *
     * @param hour is the specific hour in the week, starting with 0.
     * @return true if hour is a valid week hour, otherwise false
     */
    public boolean isValidHour(Integer hour) {
        return hour != null && hour >= Link.MIN_HOUR && hour <= Link.MAX_HOUR;
    }

    /**
     * Check if the given day number is valid.
     *
     * @param day is the specific day of the week, starting with 0, which means Sunday.
     * @return true if day is a valid day number, otherwise false
     */
    public boolean isValidDay(Integer day) {
        return day != null && day >= MIN_DAY && day <= MAX_DAY;
    }

    /**
     * Get the fill list of hour-adorator allocations.
     *
     * @return with the list of link objects
     */
    public List<Link> getLinkList() {
        List<Link> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        result = session.createQuery("from Link", Link.class).list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    /**
     * Get a specific hour-adorator Link by its ID.
     *
     * @param id of the Link
     * @return with the Link object
     */
    public Link getLink(@NotNull Long id) {
        if (id == null) {
            throw new DatabaseHandlingException("Search for Link called with null id - contact to maintainers");
        }
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Link as L where L.id = :" + EXPECTED_PARAMETER;
        Query<Link> query = session.createQuery(hql, Link.class);
        query.setParameter(EXPECTED_PARAMETER, id);
        List<Link> result = query.list();
        session.getTransaction().commit();
        session.close();
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        throw new DatabaseHandlingException("Search for Link failed with id:" + id.toString());
    }

    /**
     * Get the English standard name of the day specified as hour of the week.
     *
     * @param hourId of the week
     * @return with English standard weekday name.
     */
    public String getDayNameFromHourId(@NotNull Integer hourId) {
        int day;
        day = Math.floorDiv(hourId, HOUR_IN_A_DAY);
        String dayString;
        dayString = TranslatorDayNames.getTranslatedString(day);
        return dayString;
    }

    /**
     * Get hour in a day by specifying hour in a week.
     *
     * @param hourId in a week
     * @return hour in a day
     */
    public String getHourFromHourId(@NotNull Integer hourId) {
        int hour;
        hour = Math.floorMod(hourId, HOUR_IN_A_DAY);
        return Integer.toString(hour);
    }

    private Long linkTransaction(Session session, Link link, Collection<AuditTrail> auditTrailCollection, boolean isUpdate) {
        session.beginTransaction();
        if (isUpdate) { //BusinessBase.UPDATE
            session.update(link); //update existing
        } else { //BusinessBase.CREATE
            session.save(link);  //create new
        }
        for (AuditTrail auditTrail : auditTrailCollection) {
            session.save(auditTrail);
        }
        session.getTransaction().commit();
        session.close();
        return link.getId();
    }

    /**
     * Create a new Link record in database with its audit records.
     *
     * @param link                 is the Link to be saved
     * @param auditTrailCollection is the audit records to be saved for the new Link
     * @return with the ID of the just saved Link
     */
    public Long newLink(@NotNull Link link, @NotNull Collection<AuditTrail> auditTrailCollection) {
        Long id;
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            id = linkTransaction(session, link, auditTrailCollection, BusinessBase.CREATE);
            logger.info("Link created successfully: {}", id);
        } catch (Exception e) {
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
        return id;
    }

    /**
     * Update an existing Link object.
     *
     * @param link                 is the Link object to be updated
     * @param auditTrailCollection is the audit records of the change
     * @return with the Id of the updated Link object
     */
    public Long updateLink(@NotNull Link link, Collection<AuditTrail> auditTrailCollection) {
        Long id;
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            id = linkTransaction(session, link, auditTrailCollection, BusinessBase.UPDATE);
            logger.info("Link updated successfully: {}", id);
        } catch (Exception e) {
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
        return id;
    }

    /**
     * Delete a specific Link object.
     *
     * @param link       is the Link to be deleted
     * @param auditTrail is the audit record associated with the Link to be deleted - will be deleted as well
     * @return with the Id of the deleted Link record
     */
    public Long deleteLink(@NotNull Link link, @NotNull AuditTrail auditTrail) {
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            session.beginTransaction();
            session.delete(link);
            session.save(auditTrail);
            session.getTransaction().commit();
            session.close();
            logger.info("Link deleted successfully: {}", link.getId());
        } catch (Exception e) {
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
        return link.getId();
    }

    /**
     * Get hour-adorator link for a specific adorator.
     *
     * @param person is the adorator
     * @return with the list of Link objects
     */
    public List<Link> getLinksOfPerson(@NotNull Person person) {
        if (person == null) {
            throw new DatabaseHandlingException("getLinksOfPerson called with null parameter - contact to maintainers");
        }
        String hql = "from Link as L where L.personId = :" + EXPECTED_PARAMETER;
        return linkQuery(hql, person.getId());
    }

    /**
     * Get the list of hour-adorator Links for a specific week hour.
     *
     * @param hourId is the week hour
     * @return with the list of Link objects for the specific hour - the list has physical adorators first, then the online ones
     */
    public List<Link> getLinksOfHour(int hourId) {
        //get physical adorators ahead
        String hql = "from Link as L where L.hourId = :" + EXPECTED_PARAMETER + " order by L.type asc";
        return linkQuery(hql, hourId);
    }

    /**
     * Get list of hour-adorator Link object, but only when the adorator is in the chapel (so called physical adoration).
     *
     * @param hourId is the hour in the week
     * @return with the adorator-hour Link object list
     */
    public List<Link> getPhysicalLinksOfHour(@NotNull Integer hourId) {
        String hql = "from Link as L where L.hourId = :" + EXPECTED_PARAMETER + " and L.type = 0";
        return linkQuery(hql, hourId);
    }

    /**
     * Returns will the list of Links belong to the same hour of a week (so all links for same our of all days).
     * Only physical hours listed.
     *
     * @param hourId is the hour id in the week
     * @return with the list of Links, ordered by the hours, ascending
     */
    public List<Link> getLinksOfWeek(@NotNull Integer hourId) {
        String hql = "from Link as L where L.hourId in (:" + EXPECTED_PARAMETER + ") and L.type = 0 order by L.hourId asc";
        int i = 1;
        List<Integer> expectedHours = Arrays.asList(hourId,
                i++ * HOUR_IN_A_DAY + hourId,
                i++ * HOUR_IN_A_DAY + hourId,
                i++ * HOUR_IN_A_DAY + hourId,
                i++ * HOUR_IN_A_DAY + hourId,
                i++ * HOUR_IN_A_DAY + hourId,
                i * HOUR_IN_A_DAY + hourId
                );
        return linkQuery(hql, expectedHours);
    }

    private List<Link> linkQuery(final String hql, final Object expectedId) {
        List<Link> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        Query<Link> query = session.createQuery(hql, Link.class);
        query.setParameter(EXPECTED_PARAMETER, expectedId);
        result = query.list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    /**
     * Get previous hour in a week.
     *
     * @param hourId is the base hour id
     * @return with the id of the previous hour
     */
    public Integer getPreviousHour(@NotNull Integer hourId) {
        int previousHour;
        if (hourId > Link.MIN_HOUR) {
            previousHour = hourId - 1;
        } else {
            previousHour = Link.MAX_HOUR;
        }
        return previousHour;
    }

    /**
     * Get next hour in a week.
     *
     * @param hourId is the base hour id
     * @return with the id of the next hour
     */
    public Integer getNextHour(@NotNull Integer hourId) {
        int nextHour;
        if (hourId < Link.MAX_HOUR) {
            nextHour = hourId + 1;
        } else {
            nextHour = Link.MIN_HOUR;
        }
        return nextHour;
    }

}
