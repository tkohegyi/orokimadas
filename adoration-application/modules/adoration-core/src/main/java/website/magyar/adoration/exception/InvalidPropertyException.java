package website.magyar.adoration.exception;

/**
 * It is thrown when a property loaded from the configuration while is invalid.
 */
public class InvalidPropertyException extends SystemException {

    /**
     * Contstructs a new exception with a given <tt>message</tt>.
     *
     * @param message cause of the error
     */
    public InvalidPropertyException(final String message) {
        super(message);
    }

}
