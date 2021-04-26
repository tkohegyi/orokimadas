package website.magyar.adoration.exception;

/**
 * General exception class for AdorApp.
 */
public class SystemException extends RuntimeException {

    /**
     * Constructor that takes the exception message as input.
     *
     * @param message of the exception
     */
    public SystemException(final String message) {
        super(message);
    }

    /**
     * Constructor that takes a message and wrapped or parent exception as input.
     *
     * @param message   of the exception
     * @param throwable is the parent or wrapped exception.
     */
    public SystemException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
