package website.magyar.adoration.web.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class JspInternationalization extends SimpleTagSupport {
    private final Logger logger = LoggerFactory.getLogger(JspInternationalization.class);

    private String messageId;
    public void setMessageId(String msg) {
        this.messageId = msg;
    }

    /**
     * Gets language specific text.
     * There are 2 language specific attribute in the session:
     * - 1. "lang" attribute which is a String, and may be "en" or "hu" - the later one is the default
     * - 2. "lang2" attribute is an Environment object, and contains the property data that fits to the language code
     *
     * @throws JspException if something goes wrong
     * @throws IOException if something goes wrong
     */
    public void doTag() throws JspException, IOException {
        String text = null;
        String lang = (String)getJspContext().getAttribute("lang", PageContext.SESSION_SCOPE);
        if (lang == null) {
            lang = "hu";
            getJspContext().setAttribute("lang", lang, PageContext.SESSION_SCOPE);
            logger.warn("Language not set for session! Falling back to 'hu'.");
        }
        Environment environment = (Environment)getJspContext().getAttribute("lang2", PageContext.SESSION_SCOPE);
        if (environment != null) {
            text = environment.getProperty(lang + messageId);
        } else {
            logger.warn("Environment was not set for session!");
        }
        if (text == null) {
            text = "LanguageError - " + lang + messageId;
            String usedJsp = ((HttpServletRequest) ((PageContext)getJspContext()).getRequest()).getRequestURI();
            logger.warn("{} - path:{}", text, usedJsp);
        }
        JspWriter out = getJspContext().getOut();
        out.print(text);
    }
}
