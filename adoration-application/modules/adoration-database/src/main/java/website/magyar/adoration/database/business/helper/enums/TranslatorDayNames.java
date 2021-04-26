package website.magyar.adoration.database.business.helper.enums;

import website.magyar.adoration.database.exception.DatabaseHandlingException;

/**
 * Enum for the days of the week.
 */
public enum TranslatorDayNames {
    SUNDAY("Sunday", 0),
    MONDAY("Monday", 1),
    TUESDAY("Tuesday", 2),
    WEDNESDAY("Wednesday", 3),
    THURSDAY("Thursday", 4),
    FRIDAY("Friday", 5),
    SATURDAY("Saturday", 6);

    private final String dayText;
    private final Integer dayValue;

    TranslatorDayNames(String dayText, Integer dayValue) {
        this.dayText = dayText;
        this.dayValue = dayValue;
    }

    /**
     * Gets the name of the day given by its number.
     * @param value is the number of the day, starting with 0 that means Sunday
     * @return with the name of the specific day
     */
    public static String getTranslatedString(Integer value) {
        for (TranslatorDayNames translatorDayNames : TranslatorDayNames.values()) {
            if (translatorDayNames.dayValue.equals(value)) {
                return translatorDayNames.dayText;
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> TranslatorDayNames number:" + value.toString() + " was requested.");
    }

    public String getDayText() {
        return dayText;
    }

    public Integer getDayValue() {
        return dayValue;
    }


}
