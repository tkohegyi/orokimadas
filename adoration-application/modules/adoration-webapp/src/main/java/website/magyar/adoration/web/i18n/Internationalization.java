package website.magyar.adoration.web.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Component
public class Internationalization {

    @Autowired
    private LangProperties langProperties;

    private final Logger logger = LoggerFactory.getLogger(Internationalization.class);

    private static HashMap<String,String> languagePackHu = null;
    private static HashMap<String,String> languagePackEn = null;

    /**
     * Set language information if it is not-yet set for the session.
     * @param httpSession is the actual session
     * @param localeString is the localeString to be used
     */
    public void setLanguage(HttpSession httpSession, String localeString) {
        if (!"hu".equals(localeString) && !"en".equals(localeString)) {
            localeString = "hu";
            logger.warn("Incorrect setLanguage call - falling back to 'hu' - contact to maintainers.");
        }
        httpSession.setAttribute("lang", "i18n." + localeString + ".");
        httpSession.setAttribute("langCode", localeString);
        if (httpSession.getAttribute("lang2") == null) {
            //no locale was set to this session previously
            httpSession.setAttribute("lang2", langProperties.getEnvironment());
        }
    }

    public HashMap<String,String> getLanguagePack(String languageCode) {
        if (languagePackHu == null) { //load the lang pack only one time
            languagePackHu = langProperties.getWebStrings("hu");
            languagePackEn = langProperties.getWebStrings("en");
        }
        HashMap<String,String> langPack = languagePackHu; //default
        if (languageCode.equals("en")) {
            langPack = languagePackEn;
        }
        return langPack;
    }

    public String detectLanguage(HttpSession httpSession) {
        if ((httpSession.getAttribute("lang") == null)
                || (httpSession.getAttribute("lang2") == null)
                || (httpSession.getAttribute("langCode") == null)) {
            setLanguage(httpSession, "hu"); //default
        }
        String langCode = (String) httpSession.getAttribute("langCode");
        if (langCode == null) {
            logger.warn("Detect language failed with null langCode.");
        }
        return langCode;
    }
}
