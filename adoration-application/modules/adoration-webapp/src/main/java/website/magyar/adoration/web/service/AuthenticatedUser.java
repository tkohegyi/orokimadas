package website.magyar.adoration.web.service;

import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;

/**
 * Base class of an authenticated user.
 */
public class AuthenticatedUser {

    private static final long ONE_SEC = 1000L;
    private final long sessionTimeoutExtender;
    private final String serviceName;
    private Social social;
    private Person person;
    private long sessionTimeout;

    /**
     * initializes the object.
     *
     * @param serviceName         is the name of the Oath2 service used for authentication
     * @param social              is the Social login record used for the identification
     * @param person              is the Person associated to the specific Socual login record - if any
     * @param sessionTimeoutInSec time period of the session
     */
    public AuthenticatedUser(String serviceName, Social social, Person person, Integer sessionTimeoutInSec) {
        this.serviceName = serviceName;
        this.social = social;
        this.person = person;
        this.sessionTimeoutExtender = (long) sessionTimeoutInSec * ONE_SEC;
        extendSessionTimeout();
    }

    public Social getSocial() {
        return social;
    }

    public void setSocial(Social social) {
        this.social = social;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Extends the session validity with a preconfigured additional time period.
     */
    public void extendSessionTimeout() {
        this.sessionTimeout = System.currentTimeMillis() + sessionTimeoutExtender;
    }

    public boolean isSessionValid() {
        return this.sessionTimeout > System.currentTimeMillis();
    }

    public String getServiceName() {
        return serviceName;
    }
}
