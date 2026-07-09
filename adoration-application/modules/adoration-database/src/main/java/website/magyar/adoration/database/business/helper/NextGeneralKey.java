package website.magyar.adoration.database.business.helper;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Supporter class to get a identification number (ID) that is unique in the whole Adoration database - to identify all objects/record in a unique way.
 */
@Component
public class NextGeneralKey {

    /**
     * Generates the next general key within Adoration database. Must be called from within a transaction.
     *
     * Note that this works with PostgreSQL database only.
     *
     * In case of MSSQL database an alternative code need to be used:
     * Iterator<Long> iter;
     * Query query = session.createNativeQuery( "SELECT NEXT VALUE FOR dbo.AdorationUniqueNumber");
     * iter = (Iterator<Long>) query.getResultList();
     * iter.next().longValue();
     *
     * @return with the next key
     */
    public Long getNextGeneralKay(Session session) {
        NativeQuery<Long> query = session.createNativeQuery("select nextval('\"dbo\".\"AdorationUniqueNumber\"')", Long.class);
        List<Long> values = query.getResultList();
        return values.get(0);
    }

}
