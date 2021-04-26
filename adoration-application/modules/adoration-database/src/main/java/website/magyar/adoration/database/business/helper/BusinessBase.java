package website.magyar.adoration.database.business.helper;

import java.util.List;

/**
 * The Base class of Business classes those are handling database tables.
 * Not a must use, but helps to provide common solution for parent classes.
 */
public class BusinessBase {
    protected static final String EXPECTED_PARAMETER = "expectedParameter";
    protected static final boolean CREATE = false;
    protected static final boolean UPDATE = true;

    /**
     * Return with the first item of the list, if the list exists and has at least one item - otherwise returns with null.
     * @param result is the list, usually result of a database query
     * @return with the first item of the list, if the list exists and has at least one item - otherwise returns with null
     */
    protected Object returnWithFirstItem(final List<?> result) {
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }
}
