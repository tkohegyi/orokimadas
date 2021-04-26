package website.magyar.adoration.web.provider;

import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.tables.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class to provide information about audit records.
 */
@Component
public class AuditProvider {

    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;

    /**
     * Get list of audit records of the last N days.
     *
     * @param days is info about the requested days
     * @return with the object
     */
    public Object getAuditTrailOfLastDays(@NonNull Long days) {
        List<AuditTrail> auditTrailList;
        auditTrailList = businessWithAuditTrail.getAuditTrailOfLastDays(days);
        return auditTrailList;
    }

}
