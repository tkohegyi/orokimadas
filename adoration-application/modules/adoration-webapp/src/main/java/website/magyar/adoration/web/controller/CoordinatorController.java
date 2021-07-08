package website.magyar.adoration.web.controller;

import com.google.gson.Gson;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.json.CoordinatorJson;
import website.magyar.adoration.web.json.DeleteEntityJson;
import website.magyar.adoration.web.json.TableDataInformationJson;
import website.magyar.adoration.web.provider.CoordinatorProvider;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller for handling requests for the application pages about Coordinators.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class CoordinatorController extends ControllerBase {
    private final Logger logger = LoggerFactory.getLogger(CoordinatorController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private CoordinatorProvider coordinatorProvider;

    /**
     * Serves requests for general Coordinator info.
     *
     * @return the name of the jsp to display as result
     */
    @GetMapping(value = "/adorationSecure/coordinators")
    public String coordinators() {
        return "coordinators";
    }

    /**
     * Get full list of Coordinators.
     *
     * @param httpSession         identifies the user
     * @param httpServletResponse response settings object
     * @return with the response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getCoordinators")
    public TableDataInformationJson getCoordinators(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        TableDataInformationJson content = null;
        if (isRegisteredAdorator(currentUserProvider, httpSession)) {
            //has right to collect and see information
            var coordinators = coordinatorProvider.getCoordinatorListAsObject(currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(coordinators);
        }
        return content;
    }

    /**
     * Gets specific coordinator, specified by its Id.
     *
     * @return with the coordinator as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getCoordinator/{id:.+}")
    public TableDataInformationJson getCoordinatorById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the coordinator
            var id = Long.valueOf(requestedId);
            var coordinatorJson = coordinatorProvider.getCoordinatorAsObject(id, currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(coordinatorJson);
        }
        return content;
    }

    /**
     * Update an existing coordinator.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/updateCoordinator")
    public ResponseEntity<String> updateCoordinator(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            var currentUserInformationJson = currentUserProvider.getUserInformation(session);
            var g = new Gson();
            var p = g.fromJson(body, CoordinatorJson.class);
            //check authorization: user must have right user type
            if (!currentUserInformationJson.isAdoratorAdmin) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                Long updateInformation = coordinatorProvider.updateCoordinator(p, currentUserInformationJson);
                if (updateInformation != null) {
                    resultString = "OK-" + updateInformation.toString();
                    result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot update the Coordinator, please check the values and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot update the Coordinator with ID: {}", p.id);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot update the Coordinator, please contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at Coordinator, pls contact to maintainers", e);
        }
        return result;
    }

    /**
     * Delete an existing coordinator.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/deleteCoordinator")
    public ResponseEntity<String> deleteCoordinator(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            var currentUserInformationJson = currentUserProvider.getUserInformation(session);
            //check authorization
            if (!currentUserInformationJson.isAdoratorAdmin) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, DeleteEntityJson.class);
                var updatedObjectId = coordinatorProvider.deleteCoordinator(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot delete Coordinator, please check and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot delete Coordinator - data issue.");
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot delete Coordinator, pls contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at delete Coordinator call", e);
        }
        return result;
    }

}
