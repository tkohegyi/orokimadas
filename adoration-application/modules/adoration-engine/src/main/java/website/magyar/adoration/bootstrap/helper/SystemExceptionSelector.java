package website.magyar.adoration.bootstrap.helper;

import website.magyar.adoration.exception.SystemException;
import org.springframework.beans.factory.BeanCreationException;

/**
 * Class for selecting {@link SystemException} from exception causes to be able to get a user-readable exception message.
 */
public class SystemExceptionSelector {

    /**
     * Selecting {@link SystemException} from {@link BeanCreationException} causes if exists to be able to return with user-readable exception message.
     *
     * @param e is the catched exception
     * @return with {@link SystemException} if causes contains {@link SystemException} else null
     */
    public SystemException getSystemException(final BeanCreationException e) {
        SystemException result = null;
        Throwable ex = e;
        boolean found = false;
        while (ex.getCause() != null && !found) {
            if (ex.getCause() instanceof SystemException) {
                found = true;
                result = (SystemException) ex.getCause();
            }
            ex = ex.getCause();
        }
        return result;
    }
}
