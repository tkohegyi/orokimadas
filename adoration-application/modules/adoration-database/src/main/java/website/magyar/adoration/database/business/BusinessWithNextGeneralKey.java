package website.magyar.adoration.database.business;

import org.hibernate.Session;
import website.magyar.adoration.database.SessionFactoryHelper;
import website.magyar.adoration.database.business.helper.NextGeneralKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Business class to provide unique ID for all database item. This ID is unique id for every database item.
 */
@Component
public class BusinessWithNextGeneralKey {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithNextGeneralKey.class);

    @Autowired
    private NextGeneralKey nextGeneralKey;

    /**
     * Get the next unique Id.
     * @return with the new Id.
     */
    public Long getNextGeneralId() {
        Long id;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        id = nextGeneralKey.getNextGeneralKay(session);
        logger.debug("New sequence arrived: {}", id);
        session.getTransaction().commit();
        session.close();
        return id;
    }

}
