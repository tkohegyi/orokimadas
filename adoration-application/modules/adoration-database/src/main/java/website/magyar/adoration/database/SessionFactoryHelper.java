package website.magyar.adoration.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import website.magyar.adoration.database.exception.DatabaseHandlingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;


/**
 * This class is to provide general connection to the database server.
 * Requires an external class that has info about DB connection details - in addition to hibernate.cfg.xml.
 */
public class SessionFactoryHelper {
    private static SessionFactory sessionFactory;
    private static final Logger SESSION_FACTORY_HELPER_LOGGER = LoggerFactory.getLogger(SessionFactoryHelper.class);

    /**
     * Shuts down the connection to the Database. Must be called during application shutdown.
     * After calling this, no more database operation can be done.
     */
    public static void shutdownHibernateSessionFactory() {
        if (sessionFactory != null) {
            SESSION_FACTORY_HELPER_LOGGER.info("Closing database connection...");
            sessionFactory.close();
            SESSION_FACTORY_HELPER_LOGGER.info("Connection to database closed successfully.");
        }
    }

    /**
     * General method to access an opened session for Database operations. After using the session, the session shall be closed.
     * Used by "BusinessWith<..." classes, only.
     *
     * @return with the opened session
     */
    @NonNull
    public static Session getOpenedSession() {
        if (sessionFactory == null) {
            throw new DatabaseHandlingException("Database connection issue (SessionFactory) - pls contact to maintainers.");
        }
        return sessionFactory.openSession();
    }

    /**
     * Initialize connection to the Database in the background.
     * This method need to be invoked during the bootstrap process, and only one time.
     *
     * @param connectionUrl Database connection parameter
     * @param userName      Database connection parameter
     * @param password      Database connection parameter
     */
    public void initiateHibernateSessionFactory(final String connectionUrl, final String userName, final String password) {
        SESSION_FACTORY_HELPER_LOGGER.info("Connecting to database...");
        if ((connectionUrl == null) || (userName == null) || (password == null)) {
            throw new ExceptionInInitializerError("Unknown DB connection parameters.");
        }

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .applySetting("hibernate.connection.url", connectionUrl)
                .applySetting("hibernate.connection.username", userName)
                .applySetting("hibernate.connection.password", password)
                .build();
        try {
            createSessionFactory(registry);
            SESSION_FACTORY_HELPER_LOGGER.info("Connection to database established correctly.");
        } catch (RuntimeException e) {
            // The registry would be destroyed by the DatabaseConnection, but we had trouble building the DatabaseConnection
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw new DatabaseHandlingException("Error in creating SessionFactory object.", e);
        }
    }

    private static synchronized void createSessionFactory(final StandardServiceRegistry registry) {
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }
    
}
