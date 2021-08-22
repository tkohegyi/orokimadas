package website.magyar.adoration.web.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import website.magyar.adoration.exception.SystemException;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Component
public class Internationalization {

    @Autowired
    private LangProperties langProperties;

    private static HashMap<String,String> languagePackHu = null;
    private static HashMap<String,String> languagePackEn = null;

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
}
