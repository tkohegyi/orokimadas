package website.magyar.adoration.web.controller;

import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.json.TableDataInformationJson;
import website.magyar.adoration.web.provider.AuditProvider;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Controller for handling requests about the audit records.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class AuditController extends ControllerBase {
    private final Logger logger = LoggerFactory.getLogger(AuditController.class);
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private AuditProvider auditProvider;

    /**
     * Serves administrator request about audit records.
     *
     * @return the name of the jsp to display the result
     */
    @GetMapping(value = "/adorationSecure/audit")
    public String audit(HttpSession httpSession) {
        if (isAdoratorAdmin(currentUserProvider, httpSession)) { //only admins
            return "audit";
        }
        return REDIRECT_TO_HOME; //not admin -> go back to basic home page
    }

    /**
     * Get list of audit records of the last N (=requestedDays) days.
     *
     * @param httpSession   identifies the user
     * @param requestedDays are the last N days for the query, must be > 0
     * @return with proper content
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getAuditTrailByDays/{days:.+}")
    public TableDataInformationJson getAuditTrailByDays(HttpSession httpSession, @PathVariable("days") final String requestedDays) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //has right to collect and see information
            try {
                var days = Long.parseLong(requestedDays);
                var information = auditProvider.getAuditTrailOfLastDays(days);
                content = new TableDataInformationJson(information);
            } catch (NumberFormatException e) {
                logger.warn("Rouge request to getAuditTrailByDays endpoint with bad days parameter.");
            }
        }
        return content;
    }

}
