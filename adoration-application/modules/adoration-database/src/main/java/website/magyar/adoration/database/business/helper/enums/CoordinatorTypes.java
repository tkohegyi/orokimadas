package website.magyar.adoration.database.business.helper.enums;

import website.magyar.adoration.database.exception.DatabaseHandlingException;

/**
 * Enum of Coordinator Types.
 */
public enum CoordinatorTypes {
    HOURLY_COORDINATOR_0("Hourly Coordinator: 0", 0),
    HOURLY_COORDINATOR_1("Hourly Coordinator: 1", 1),
    HOURLY_COORDINATOR_2("Hourly Coordinator: 2", 2),
    HOURLY_COORDINATOR_3("Hourly Coordinator: 3", 3),
    HOURLY_COORDINATOR_4("Hourly Coordinator: 4", 4),
    HOURLY_COORDINATOR_5("Hourly Coordinator: 5", 5),
    HOURLY_COORDINATOR_6("Hourly Coordinator: 6", 6),
    HOURLY_COORDINATOR_7("Hourly Coordinator: 7", 7),
    HOURLY_COORDINATOR_8("Hourly Coordinator: 8", 8),
    HOURLY_COORDINATOR_9("Hourly Coordinator: 9", 9),
    HOURLY_COORDINATOR_10("Hourly Coordinator: 10", 10),
    HOURLY_COORDINATOR_11("Hourly Coordinator: 11", 11),
    HOURLY_COORDINATOR_12("Hourly Coordinator: 12", 12),
    HOURLY_COORDINATOR_13("Hourly Coordinator: 13", 13),
    HOURLY_COORDINATOR_14("Hourly Coordinator: 14", 14),
    HOURLY_COORDINATOR_15("Hourly Coordinator: 15", 15),
    HOURLY_COORDINATOR_16("Hourly Coordinator: 16", 16),
    HOURLY_COORDINATOR_17("Hourly Coordinator: 17", 17),
    HOURLY_COORDINATOR_18("Hourly Coordinator: 18", 18),
    HOURLY_COORDINATOR_19("Hourly Coordinator: 19", 19),
    HOURLY_COORDINATOR_20("Hourly Coordinator: 20", 20),
    HOURLY_COORDINATOR_21("Hourly Coordinator: 21", 21),
    HOURLY_COORDINATOR_22("Hourly Coordinator: 22", 22),
    HOURLY_COORDINATOR_23("Hourly Coordinator: 23", 23),
    NIGHT_COORDINATOR("Night Coordinator", 24),
    MORNING_COORDINATOR("Morning Coordinator", 24 + 6),
    AFTERNOON_COORDINATOR("Afternoon Coordinator", 24 + 12),
    EVENING_COORDINATOR("Evening Coordinator", 24 + 18),
    GENERAL_COORDINATOR("General Coordinator", 48),
    SPIRITUAL_COORDINATOR("Spiritual Coordinator", 96);

    private final String coordinatorText;
    private final Integer coordinatorValue;

    CoordinatorTypes(String coordinatorText, Integer coordinatorValue) {
        this.coordinatorText = coordinatorText;
        this.coordinatorValue = coordinatorValue;
    }

    /**
     * Get CoordinatorType enum specified by its Id.
     *
     * @param coordinatorType is the enum we are searching for
     * @return with the CoordinatorType the given type id matches
     */
    public static CoordinatorTypes getTypeFromId(Integer coordinatorType) {
        for (CoordinatorTypes coordinatorTypes : CoordinatorTypes.values()) {
            if (coordinatorTypes.coordinatorValue.equals(coordinatorType)) {
                return coordinatorTypes;
            }
        }
        throw new DatabaseHandlingException("Invalid CoordinatorType requested: " + coordinatorType);
    }

    public String getCoordinatorText() {
        return coordinatorText;
    }

    public Integer getCoordinatorValue() {
        return coordinatorValue;
    }
}
