package website.magyar.adoration.web.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import website.magyar.adoration.exception.SystemException;

import javax.servlet.http.HttpSession;

@Component
public class Internationalization {

    @Autowired
    private LangProperties langProperties;

    /**
     * Set language information if it is not-yet set for the session.
     * @param httpSession is the actual session
     * @param localeString is the localeString to be used
     */
    public void setLanguage(HttpSession httpSession, String localeString) {
        if (!"hu".equals(localeString) && !"en".equals(localeString)) {
            throw new SystemException("Incorrect setLanguage call - contact to maintainers.");
        }
        httpSession.setAttribute("lang", "i18n." + localeString + ".");
        if (httpSession.getAttribute("lang2") == null) {
            //no locale was set to this session previously
            httpSession.setAttribute("lang2", langProperties.getEnvironment());
        }
    }

}
