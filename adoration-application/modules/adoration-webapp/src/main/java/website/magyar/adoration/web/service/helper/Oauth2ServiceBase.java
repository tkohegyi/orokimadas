package website.magyar.adoration.web.service.helper;

/**
 * Base class of Oauth2Service classes.
 */
public class Oauth2ServiceBase {
    protected static final String AUDIT_SOCIAL_UPDATE = "Social:Update:";
    protected static final String AUDIT_SOCIAL_CREATE = "Social:New:";

    /**
     * Utility method to ensure that a string is not null.
     *
     * @return with the inputString unchanged, or with an empty string in case the input was null
     */
    protected String makeEmptyStringFromNull(String inputString) {
        String notNullString = inputString;
        if (inputString == null) {
            notNullString = "";
        }
        return notNullString;
    }

}
