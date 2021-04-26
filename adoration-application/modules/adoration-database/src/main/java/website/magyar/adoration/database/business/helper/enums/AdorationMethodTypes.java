package website.magyar.adoration.database.business.helper.enums;

import website.magyar.adoration.database.exception.DatabaseHandlingException;

/**
 * Enum of possible adoration method types, those are either "Physical" or "Online".
 * Physical means the adoration happens in the chapel.
 * Online means that adoration happens in front of a screen, where the screen shows the picture of the camera located inside the chapel.
 * The last two is used when a person registers for a single hour (One-Time participation/On)
 * and when a person declares that cannot participate (One-Time Miss/Off)
 * These two later ones are temporary ones, and deleted by the system after the event.
 * Of course, these are valid for "Physical" adoration only.
 */
public enum AdorationMethodTypes {
    PHYSICAL("Physical", 0),
    ONLINE("Online", 1),
    ONETIME_ON("OneTime Participate", 2),
    ONETIME_OFF("OneTime Miss", 3);

    private final String adorationMethodText;
    private final Integer adorationMethodValue;

    AdorationMethodTypes(String adorationMethodText, Integer adorationMethodValue) {
        this.adorationMethodText = adorationMethodText;
        this.adorationMethodValue = adorationMethodValue;
    }

    // helper functions

    /**
     * Gets adoration method as string.
     *
     * @param adorationMethodValue is the ID of the adoration method type
     * @return with the adoration method as string
     */
    public static String getTranslatedString(Integer adorationMethodValue) {
        for (AdorationMethodTypes adorationMethodTypes : AdorationMethodTypes.values()) {
            if (adorationMethodTypes.getAdorationMethodValue().equals(adorationMethodValue)) {
                return adorationMethodTypes.getAdorationMethodText();
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> AdorationMethodType number:" + adorationMethodValue.toString() + " was requested.");
    }

    /**
     * Gets adoration method enum from its ID.
     *
     * @param id is the Id of the Enum
     * @return with the Enum itself
     */
    public static AdorationMethodTypes getTypeFromId(Integer id) {
        for (AdorationMethodTypes adorationMethodTypes : AdorationMethodTypes.values()) {
            if (adorationMethodTypes.getAdorationMethodValue().equals(id)) {
                return adorationMethodTypes;
            }
        }
        throw new DatabaseHandlingException("Invalid AdorationMethodType requested: " + id);
    }

    public String getAdorationMethodText() {
        return adorationMethodText;
    }

    public Integer getAdorationMethodValue() {
        return adorationMethodValue;
    }
}
