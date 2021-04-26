package website.magyar.adoration.database.business;

import com.sun.istack.NotNull;  //NOSONAR
import org.hibernate.Session;
import org.hibernate.query.Query;
import website.magyar.adoration.database.SessionFactoryHelper;
import website.magyar.adoration.database.business.helper.BusinessBase;
import website.magyar.adoration.database.tables.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Business class to handle Translator Database table, that represents language translations.
 * Only partially used on the we site, but will be used as base of a multi-language adoration page.
 */
@Component
public class BusinessWithTranslator extends BusinessBase {

    private final Logger logger = LoggerFactory.getLogger(BusinessWithTranslator.class);

    /**
     * Gets List of all translated words based on a specified language code.
     *
     * @return with the list.
     */
    public List<Translator> getTranslatorList(@NotNull String languageCode) {
        List<Translator> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Translator as T where T.languageCode like :languageCode";
        Query<Translator> query = session.createQuery(hql, Translator.class);
        query.setParameter("languageCode", languageCode);
        result = query.list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    /**
     * Get translated text value based on language code an text id.
     *
     * @param languageCode is the specific language code
     * @param textId       is the text id to be translated
     * @param defaultValue is the default return value in case the specific languageCode + textId cannot be found
     * @return with the text translated to the specific language - or the default if translation was not found
     */
    public String getTranslatorValue(@NotNull String languageCode, @NotNull String textId, @NotNull String defaultValue) {
        String result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Translator as T where T.languageCode like :languageCode and T.textId like :textId";
        Query<Translator> query = session.createQuery(hql, Translator.class);
        query.setParameter("languageCode", languageCode);
        query.setParameter("textId", textId);
        List<Translator> qResult = query.list();
        session.getTransaction().commit();
        session.close();
        Translator translator = (Translator) returnWithFirstItem(qResult);
        if (translator == null) {
            result = defaultValue;
            logger.warn("Unable to find translator text for languageCode/textId pair: {}/{}", languageCode, textId);
        } else {
            result = translator.getText();
        }
        return result;
    }
}
