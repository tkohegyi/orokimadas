package website.magyar.adoration.database.business;

import com.sun.istack.NotNull; //NOSONAR
import org.hibernate.Session;
import org.hibernate.query.Query;
import website.magyar.adoration.database.SessionFactoryHelper;
import website.magyar.adoration.database.business.helper.BusinessBase;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Coordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Business class to handle Coordinator Database table.
 * This table holds information about hourly, daily and overall coordinators, and special (like spiritual) leaders.
 */
@Component
public class BusinessWithCoordinator extends BusinessBase {
    public static final Integer QUARTER_DAY_IN_HOURS = 6;
    private final Logger logger = LoggerFactory.getLogger(BusinessWithCoordinator.class);

    /**
     * Create a new Coordinator record.
     *
     * @param newC       is the Coordinator to be created
     * @param auditTrail is the associated audit objects
     * @return with the ID of the new Coordinator
     */
    public Long newCoordinator(@NotNull Coordinator newC, @NotNull AuditTrail auditTrail) {
        Long id = null;
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            session.beginTransaction();
            session.save(newC); //insert into table !
            session.save(auditTrail);
            session.getTransaction().commit();
            id = newC.getId();
            logger.info("Coordinator record created successfully: {}", id);
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.warn("Coordinator record creation failure", e);
        }
        session.close();
        return id;
    }

    /**
     * Get full list of Coordinators (hourly, daily, general, spiritual etc).
     *
     * @return with the list
     */
    public List<Coordinator> getList() {
        List<Coordinator> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        result = session.createQuery("from Coordinator", Coordinator.class).list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    /**
     * Get Coordinator by its unique ID.
     *
     * @param id is its ID.
     * @return with the Coordinator found. Returns null if not found.
     */
    public Coordinator getById(@NotNull Long id) {
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Coordinator as C where C.id = :" + EXPECTED_PARAMETER;
        Query<Coordinator> query = session.createQuery(hql, Coordinator.class);
        query.setParameter(EXPECTED_PARAMETER, id);
        List<Coordinator> result = query.list();
        session.getTransaction().commit();
        session.close();
        return (Coordinator) returnWithFirstItem(result);
    }

    /**
     * Delete a Coordinator and the associated audit records.
     *
     * @param coordinator    is the Coordinator to be deleted
     * @param auditTrailList is the related audit records to be deleted
     * @return with the ID of the deleted Coordinator
     */
    public Long deleteCoordinator(@NotNull Coordinator coordinator, List<AuditTrail> auditTrailList) {
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            session.beginTransaction();
            if (auditTrailList != null) {
                for (AuditTrail auditTrail : auditTrailList) {
                    session.delete(auditTrail);
                }
            }
            session.delete(coordinator);
            session.getTransaction().commit();
            session.close();
            logger.info("Coordinator deleted successfully: {}", coordinator.getId());
        } catch (Exception e) {
            session.getTransaction().rollback();
            session.close();
            logger.info("Coordinator delete failed: {}", coordinator.getId());
            throw e;
        }
        return coordinator.getId();
    }

    /**
     * Update an existing Coordinator record.
     *
     * @param coordinator          is the record to be updated
     * @param auditTrailCollection is the audit records about the changes
     * @return with the ID of the updated record
     */
    public Long updateCoordinator(@NotNull Coordinator coordinator, @NotNull Collection<AuditTrail> auditTrailCollection) {
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            session.beginTransaction();
            session.update(coordinator);
            for (AuditTrail auditTrail : auditTrailCollection) {
                session.save(auditTrail);
            }
            session.getTransaction().commit();
            session.close();
            logger.info("Coordinator updated successfully: {}", coordinator.getId());
        } catch (Exception e) {
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
        return coordinator.getId();
    }

    /**
     * Returns with list of Coordinators, but only the main coordinators.
     * So
     * - Spiritual Coo
     * - General coo
     * - Daily Coo
     * (so where the coordinatorType is > 23)
     *
     * @return with the list of such coordinators.
     */
    public List<Coordinator> getLeadership() {
        List<Coordinator> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        result = session.createQuery("from Coordinator as C where C.coordinatorType > 23 order by C.coordinatorType asc", Coordinator.class).list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    /**
     * Coordinator search by person ID - used to detect if a person is a coordinator too.
     * In case a person has multiple roles (like somebody is both hourly and daily coordinator),
     * the highest will be selected - or null, if the Person is not a Coordinator.
     *
     * @param id is the ID of the person
     * @return with the Coordinator object of the person - or null if it does not exist
     */
    public Coordinator getCoordinatorFromPersonId(@NotNull Long id) {
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Coordinator as C where C.personId = :" + EXPECTED_PARAMETER + " order by C.coordinatorType desc";
        Query<Coordinator> query = session.createQuery(hql, Coordinator.class);
        query.setParameter(EXPECTED_PARAMETER, id);
        List<Coordinator> result = query.list();
        session.getTransaction().commit();
        session.close();
        return (Coordinator) returnWithFirstItem(result);
    }

    private Coordinator getByCoordinatorType(@NotNull Integer i) {
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Coordinator as C where C.coordinatorType = :" + EXPECTED_PARAMETER;
        Query<Coordinator> query = session.createQuery(hql, Coordinator.class);
        query.setParameter(EXPECTED_PARAMETER, i);
        List<Coordinator> result = query.list();
        session.getTransaction().commit();
        session.close();
        return (Coordinator) returnWithFirstItem(result);
    }

    /**
     * Gets the responsible Daily Coordinator of the specified hour.
     *
     * @param hour is the specific hour
     * @return with the respective Daily Coordinator
     */
    public Coordinator getDailyCooOfHour(@NotNull Integer hour) {
        if (hour >= BusinessWithLink.HOUR_IN_A_DAY) {
            return null;
        }
        int dayPart = hour / QUARTER_DAY_IN_HOURS;
        return getByCoordinatorType(BusinessWithLink.HOUR_IN_A_DAY + dayPart * BusinessWithLink.HOUR_IN_A_DAY);
    }

    /**
     * Gets the responsible Hourly Coordinator of the specified hour.
     *
     * @param hour is the specific hour
     * @return with the respective Hourly Coordinator (or null if it is missing)
     */
    public Coordinator getHourlyCooOfHour(@NotNull Integer hour) {
        return getByCoordinatorType(hour);
    }
}
