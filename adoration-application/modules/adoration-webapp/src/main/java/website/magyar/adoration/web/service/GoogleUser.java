package website.magyar.adoration.web.service;

import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;

/**
 * Google type of AuthenticatedUser.
 */
public class GoogleUser extends AuthenticatedUser {

    /**
     * Creates a Google User login class.
     *
     * @param social              is the associated Social login record.
     * @param person              is the associated Person record (real life person) if exists
     * @param sessionTimeoutInSec determines the validity of he session
     */
    public GoogleUser(Social social, Person person, Integer sessionTimeoutInSec) {
        super("Google", social, person, sessionTimeoutInSec);
    }

}
