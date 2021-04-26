package website.magyar.adoration.database.business.helper.enums;

import website.magyar.adoration.database.exception.DatabaseHandlingException;

/**
 * Enum to represent status of a Social record.
 */
public enum SocialStatusTypes {
    WAIT_FOR_IDENTIFICATION("Waiting for Identification", 1),
    IDENTIFIED_USER("Identified User", 2),
    SOCIAL_USER("Social-only User", 3);

    private final String translatedText;
    private final Integer typeValue;

    SocialStatusTypes(String translatedText, Integer typeValue) {
        this.translatedText = translatedText;
        this.typeValue = typeValue;
    }

    /**
     * Get Social status as a string.
     *
     * @param typeValue is the Id based identified of the enum.
     * @return with the string representation of the enum identified by the typeValue
     * @throws DatabaseHandlingException in case the give Id is invalid
     */
    public static String getTranslatedString(Integer typeValue) {
        for (SocialStatusTypes socialStatusTypes : SocialStatusTypes.values()) {
            if (socialStatusTypes.typeValue.equals(typeValue)) {
                return socialStatusTypes.getTranslatedText();
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> SocialStatusTypes type:" + typeValue.toString() + " was requested.");
    }

    /**
     * Gets the enum specified by its Id.
     *
     * @param id is the id that identified the Social ytpe enum
     * @return with the enum identified by the specified Id
     * @throws DatabaseHandlingException in case the specified Id is invalid
     */
    public static SocialStatusTypes getTypeFromId(Integer id) {
        for (SocialStatusTypes socialStatusTypes : SocialStatusTypes.values()) {
            if (socialStatusTypes.typeValue.equals(id)) {
                return socialStatusTypes;
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> SocialStatusTypes id:" + id.toString() + " was requested.");
    }

    // helper functions

    public Integer getTypeValue() {
        return typeValue;
    }

    public String getTranslatedText() {
        return translatedText;
    }

}
