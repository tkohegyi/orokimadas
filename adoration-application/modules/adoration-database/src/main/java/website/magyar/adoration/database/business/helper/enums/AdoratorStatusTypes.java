package website.magyar.adoration.database.business.helper.enums;

import website.magyar.adoration.database.exception.DatabaseHandlingException;

import java.util.HashSet;
import java.util.Set;

/**
 * Enum of the adorator status from Pre-Adorator through Adorator till Post or Dead Adorator.
 */
public enum AdoratorStatusTypes {
    USER("External User/Adorator", 0),
    PRE_ADORATOR("Pre-Adorator", 1),
    ADORATOR("Adorator", 2),
    POST_ADORATOR("Post-Adorator", 3),
    DIED_ADORATOR("Died", 4),
    REGISTERED_BY_MISTAKE("Registered by mistake", 5),
    ADORATOR_EMPHASIZED("Emphasized Adorator", 6), //for Margit and all coordinators
    ADORATOR_ADMIN("Administrator", 7);

    private static final Set<AdoratorStatusTypes> REGISTERED_ADORATOR;
    private static final Set<AdoratorStatusTypes> LEADERS;
    private static final Set<AdoratorStatusTypes> ADMINS;

    private static final Set<AdoratorStatusTypes> INACTIVE_ADORATORS;

    static {
        INACTIVE_ADORATORS = new HashSet<>();
        INACTIVE_ADORATORS.add(AdoratorStatusTypes.REGISTERED_BY_MISTAKE);
        INACTIVE_ADORATORS.add(AdoratorStatusTypes.PRE_ADORATOR);
        INACTIVE_ADORATORS.add(AdoratorStatusTypes.POST_ADORATOR);
        INACTIVE_ADORATORS.add(AdoratorStatusTypes.DIED_ADORATOR);

        REGISTERED_ADORATOR = new HashSet<>();
        REGISTERED_ADORATOR.add(AdoratorStatusTypes.ADORATOR_ADMIN);
        REGISTERED_ADORATOR.add(AdoratorStatusTypes.ADORATOR);
        REGISTERED_ADORATOR.add(AdoratorStatusTypes.ADORATOR_EMPHASIZED);

        LEADERS = new HashSet<>();
        LEADERS.add(AdoratorStatusTypes.ADORATOR_ADMIN);
        LEADERS.add(AdoratorStatusTypes.ADORATOR_EMPHASIZED);

        ADMINS = new HashSet<>();
        ADMINS.add(AdoratorStatusTypes.ADORATOR_ADMIN);

    }

    private final String adoratorStatusText;
    private final Integer adoratorStatusValue;

    AdoratorStatusTypes(String adoratorStatusText, Integer adoratorStatusValue) {
        this.adoratorStatusText = adoratorStatusText;
        this.adoratorStatusValue = adoratorStatusValue;
    }

    // helper functions
    public static Set<AdoratorStatusTypes> getRegisteredAdoratorSet() {
        return REGISTERED_ADORATOR;
    }

    public static Set<AdoratorStatusTypes> getLeadersSet() {
        return LEADERS;
    }

    public static Set<AdoratorStatusTypes> getAdminsSet() {
        return ADMINS;
    }

    /**
     * Get adorator status as string.
     *
     * @param adoratorStatusValue is the Id value of the adorator status
     * @return with the string value of hte adorator status
     */
    public static String getTranslatedString(Integer adoratorStatusValue) {
        for (AdoratorStatusTypes adoratorStatusTypes : AdoratorStatusTypes.values()) {
            if (adoratorStatusTypes.getAdoratorStatusValue().equals(adoratorStatusValue)) {
                return adoratorStatusTypes.getAdoratorStatusText();
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> AdoratorStatusTypes number:" + adoratorStatusValue.toString() + " was requested.");
    }

    /**
     * Get adorator type ENum from its Id.
     *
     * @param id is the id of the Adorator Enum Type
     * @return with the Adorator status Enum
     */
    public static AdoratorStatusTypes getTypeFromId(Integer id) {
        for (AdoratorStatusTypes adoratorStatusTypes : AdoratorStatusTypes.values()) {
            if (adoratorStatusTypes.adoratorStatusValue.equals(id)) {
                return adoratorStatusTypes;
            }
        }
        throw new DatabaseHandlingException("Invalid AdoratorStatusTypes requested: " + id);
    }

    /**
     * Check if a specific adorator status is inactive or active.
     *
     * @param typeId is the adorator status type Id
     * @return true if the adorator type means that the adorator is inactive (not doing adoration), otherwise false
     */
    public static boolean isInactive(Integer typeId) {
        AdoratorStatusTypes adoratorStatusTypes = getTypeFromId(typeId);
        return INACTIVE_ADORATORS.contains(adoratorStatusTypes);
    }

    public Integer getAdoratorStatusValue() {
        return adoratorStatusValue;
    }

    public String getAdoratorStatusText() {
        return adoratorStatusText;
    }
}
