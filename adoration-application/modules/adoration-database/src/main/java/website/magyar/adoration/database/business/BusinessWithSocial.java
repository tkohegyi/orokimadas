package website.magyar.adoration.database.business;

import com.sun.istack.NotNull; //NOSONAR
import org.hibernate.Session;
import org.hibernate.query.Query;
import website.magyar.adoration.database.SessionFactoryHelper;
import website.magyar.adoration.database.business.helper.BusinessBase;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Business class to handle Social Database table, that represents (OAUTH2 based) social logins.
 * Also connects a social login to a real person, as possible.
 */
@Component
public class BusinessWithSocial extends BusinessBase {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithSocial.class);

    /**
     * Create new Social object - a connector object between Person and a social (OAUTH2) login.
     *
     * @param newSocial  is the object to be saved
     * @param auditTrail is the connected audit record
     * @return with the Id of the Social object
     */
    public Long newSocial(@NotNull Social newSocial, @NotNull AuditTrail auditTrail) {
        Long id = null;
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            session.beginTransaction();
            session.save(newSocial); //insert into Social table !
            session.save(auditTrail);
            session.getTransaction().commit();
            id = newSocial.getId();
            logger.info("Social record created successfully: {}", id);
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.warn("Social record creation failure", e);
        }
        session.close();
        return id;
    }

    //GOOGLE METHODS ===================================================================================================

    /**
     * Get (search for) a specific Social record by its Google User Id.
     *
     * @param googleUserId is the searched id
     * @return with the Social object or null
     */
    public Social getSocialByGoogleUserId(@NotNull final String googleUserId) {
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Social as S where S.googleUserId like :likeValue";
        Query<Social> query = session.createQuery(hql, Social.class);
        query.setParameter("likeValue", googleUserId);
        List<Social> result = query.list();
        session.getTransaction().commit();
        session.close();
        return (Social) returnWithFirstItem(result);
    }

    //FACEBOOK METHODS =================================================================================================

    /**
     * Get (search for) a specific Social record by its Facebook User Id.
     *
     * @param facebookUserId is the searched id
     * @return with the Social object or null
     */
    public Social getSocialByFacebookUserId(@NotNull final String facebookUserId) {
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Social as S where S.facebookUserId like :likeValue";
        Query<Social> query = session.createQuery(hql, Social.class);
        query.setParameter("likeValue", facebookUserId);
        List<Social> result = query.list();
        session.getTransaction().commit();
        session.close();
        return (Social) returnWithFirstItem(result);
    }

    /**
     * Gets associated Social records of a Person.
     *
     * @param person is the person we looking fro among Social records
     * @return with the list of the associated Social records
     */
    public List<Social> getSocialsOfPerson(@NotNull Person person) {
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Social as S where S.personId = :" + EXPECTED_PARAMETER;
        Query<Social> query = session.createQuery(hql, Social.class);
        query.setParameter(EXPECTED_PARAMETER, person.getId());
        List<Social> result = query.list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    /**
     * Get full list of Social records.
     *
     * @return with the list
     */
    public List<Social> getSocialList() {
        List<Social> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        result = session.createQuery("from Social", Social.class).list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    /**
     * Get (search for) a specific Social record with a specific Id.
     *
     * @param id is the Id of the Social record we are looking for
     * @return with the Social record, of null if not found
     */
    public Social getSocialById(@NotNull Long id) {
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Social as S where S.id = :" + EXPECTED_PARAMETER;
        Query<Social> query = session.createQuery(hql, Social.class);
        query.setParameter(EXPECTED_PARAMETER, id);
        List<Social> result = query.list();
        session.getTransaction().commit();
        session.close();
        return (Social) returnWithFirstItem(result);
    }

    /**
     * Delete a Social record.
     *
     * @param social         is the record to be deleted
     * @param auditTrailList is the list of associated audit records to be deleted as well
     * @return with the Id of the Social record that has been deleted
     */
    public Long deleteSocial(@NotNull Social social, List<AuditTrail> auditTrailList) {
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            session.beginTransaction();
            if (auditTrailList != null) {
                for (AuditTrail auditTrail : auditTrailList) {
                    session.delete(auditTrail);
                }
            }
            session.delete(social);
            session.getTransaction().commit();
            session.close();
            logger.info("Social deleted successfully: {}", social.getId());
        } catch (Exception e) {
            session.getTransaction().rollback();
            session.close();
            logger.info("Social delete failed: {}", social.getId());
            throw e;
        }
        return social.getId();
    }

    /**
     * Update an existing Social record.
     *
     * @param social               is the record to be updated
     * @param auditTrailCollection is the list of associated audit records, reflecting the changes
     * @return with the Id of the changed Social record
     */
    public Long updateSocial(@NotNull Social social, Collection<AuditTrail> auditTrailCollection) {
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            session.beginTransaction();
            session.update(social);
            for (AuditTrail auditTrail : auditTrailCollection) {
                session.save(auditTrail);
            }
            session.getTransaction().commit();
            session.close();
            logger.info("Social updated successfully: {}", social.getId());
        } catch (Exception e) {
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
        return social.getId();
    }

}
