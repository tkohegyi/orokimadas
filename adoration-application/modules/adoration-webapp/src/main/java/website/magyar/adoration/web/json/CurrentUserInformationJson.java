package website.magyar.adoration.web.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import website.magyar.adoration.database.business.BusinessWithLink;
import website.magyar.adoration.database.business.helper.enums.AdoratorStatusTypes;
import website.magyar.adoration.database.business.helper.enums.TranslatorDayNames;
import website.magyar.adoration.database.tables.Coordinator;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;
import website.magyar.adoration.helper.JsonField;

import java.util.HashMap;
import java.util.Map;

/**
 * Json structure to hold information about the actual user.
 */
public class CurrentUserInformationJson {

    private static final String EMPTY_STRING = "";
    private final Logger logger = LoggerFactory.getLogger(CurrentUserInformationJson.class);

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
    @JsonField
    public HashMap<String, String> languagePack;

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
        languagePack = null;
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
        languageCode = person.getLanguageCode();
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

    public void fillLanguagePack(HashMap<String, String> languagePack) {
        this.languagePack = languagePack;
    }

    /**
     * Loads user language dependant web.[lang].[messageId] string.
     *
     * @param messageId is the id of the language dependent text.
     *
     * @return with the text
     */
    public String getLanguageString(String messageId) {
        String text;
        if (languageCode != null) {
            text = languagePack.get(messageId);
            if (text == null) {
                text = EMPTY_STRING;
                logger.warn("Cannot load user dependent language text. MessageId:{}", messageId);
            }
        } else {
            text = EMPTY_STRING;
            logger.warn("LanguagePack is missing for user: {}", userName);
        }
        return text;
    }

    /**
     * Return map of day names in user language.
     *
     * @return with the map.
     */
    public Map<Integer, String> getUserDayNames() {
        Map<Integer, String> dayNames = new HashMap<>();
        for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
            String value = getLanguageString("common.day." + dayName.getDayValue().toString());
            dayNames.put(dayName.getDayValue(), value);
        }
        return dayNames;
    }
}
