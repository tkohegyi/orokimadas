package website.magyar.adoration.properties.helper;


import website.magyar.adoration.exception.SystemException;

/**
 * Thrown when the configuration properties cannot be loaded.
 */
public class PropertiesNotAvailableException extends SystemException {

    /**
     * Constructor with a cause.
     *
     * @param message   the message of the exception
     * @param throwable the cause of the exception
     */
    public PropertiesNotAvailableException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructor with a message only.
     *
     * @param message the message of the exception
     */
    public PropertiesNotAvailableException(final String message) {
        super(message);
    }

}
