package website.magyar.adoration.web.json;

import website.magyar.adoration.database.business.BusinessWithLink;
import website.magyar.adoration.database.business.helper.enums.AdoratorStatusTypes;
import website.magyar.adoration.database.tables.Coordinator;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;
import website.magyar.adoration.helper.JsonField;

/**
 * Json structure to hold information about the actual user.
 */
public class CurrentUserInformationJson {
    @JsonField
    public boolean isLoggedIn;
    @JsonField
    public boolean isAuthorized;
    @JsonField
    public Long personId;
    @JsonField
    public Long socialId;
    @JsonField
    public String socialEmail;
    @JsonField
    public String loggedInUserName;
    @JsonField
    public String userName;
    @JsonField
    public String languageCode;
    @JsonField
    public boolean isRegisteredAdorator;
    @JsonField
    public boolean isPrivilegedAdorator;
    @JsonField
    public boolean isAdoratorAdmin;
    @JsonField
    public Integer coordinatorId;  // id of a coordinator or -1 otherwise
    @JsonField
    public boolean isDailyCoordinator;
    @JsonField
    public boolean isHourlyCoordinator;
    @JsonField
    public String socialServiceUsed;
    @JsonField
    public String adorationApplicationVersion;

    /**
     * Constructor - fills the json structure with default values.
     */
    public CurrentUserInformationJson() {
        reset();
    }

    /**
     * Fill the json structure with basic and default (user not logged in) information.
     */
    public void reset() {
        personId = null;
        socialId = null;
        socialEmail = "";
        isLoggedIn = false;
        isAuthorized = false;
        loggedInUserName = "Anonymous";
        userName = loggedInUserName;
        languageCode = "hu"; //default language of the site
        isRegisteredAdorator = false;
        isPrivilegedAdorator = false;
        isAdoratorAdmin = false;
        socialServiceUsed = "Undetermined";
        adorationApplicationVersion = "Örökimádás applikáció - ismeretlen verzió.";
    }

    public boolean isPrivilegedUser() {
        return isPrivilegedAdorator || isAdoratorAdmin;
    }

    /**
     * Fill json fields from a given Person.
     *
     * @param person      is the person
     * @param coordinator in case the person is a coordinator, holds the most important coordinator rank of the person
     */
    public void fillIdentifiedPersonFields(Person person, Coordinator coordinator) {
        if (coordinator != null) {
            coordinatorId = coordinator.getCoordinatorType();
            isHourlyCoordinator = coordinatorId < BusinessWithLink.HOUR_IN_A_DAY;
            isDailyCoordinator = !isHourlyCoordinator;
        }
        isAuthorized = true; //not just logged in, but since the person is known, authorized too
        personId = person.getId();
        userName = person.getName();
        AdoratorStatusTypes status = AdoratorStatusTypes.getTypeFromId(person.getAdorationStatus());
        isRegisteredAdorator = AdoratorStatusTypes.getRegisteredAdoratorSet().contains(status);
        isPrivilegedAdorator = AdoratorStatusTypes.getLeadersSet().contains(status) || coordinatorId > -1;
        isAdoratorAdmin = AdoratorStatusTypes.getAdminsSet().contains(status);
    }

    /**
     * Fills json fields from the Social data.
     *
     * @param social is the Social data
     */
    public void fillIdentifiedSocialFields(Social social) {
        socialId = social.getId();
        String email = social.getGoogleEmail();
        if (email.length() == 0) {
            email = social.getFacebookEmail();
        }
        socialEmail = email;
    }

    /**
     * Fill Application version.
     */
    public void fillApplicationVersion(final String adorationApplicationVersion) {
        this.adorationApplicationVersion = adorationApplicationVersion;
    }
}
