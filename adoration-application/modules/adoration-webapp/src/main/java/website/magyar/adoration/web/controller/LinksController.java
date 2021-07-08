package website.magyar.adoration.web.controller;

import com.google.gson.Gson;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.json.DeleteEntityJson;
import website.magyar.adoration.web.json.TableDataInformationJson;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.LinkProvider;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Controller for accessing the hour assignments.
 */
@Controller
public class LinksController extends ControllerBase {
    private final Logger logger = LoggerFactory.getLogger(LinksController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private LinkProvider linkProvider;

    /**
     * Serves the links page.
     *
     * @return the name of the adorators jsp file
     */
    @GetMapping(value = "/adorationSecure/links")
    public String adorators(HttpSession httpSession) {
        if (!isAdoratorAdmin(currentUserProvider, httpSession)) {
            return REDIRECT_TO_HOME;
        }
        return "links";
    }

    /**
     * Gets the list of Links.
     *
     * @return with the list of people as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getLinkTable")
    public TableDataInformationJson getLinkTable(HttpSession httpSession, @RequestParam("filter") Optional<String> filter) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the link table
            var o = linkProvider.getLinkListAsObject(currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(o);
        }
        return content;
    }

    /**
     * Gets log history of a specific link.
     *
     * @return with the link history as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getLinkHistory/{id:.+}")
    public TableDataInformationJson getLinkHistoryById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the history
            var id = Long.valueOf(requestedId);
            var history = linkProvider.getLinkHistoryAsObject(id);
            content = new TableDataInformationJson(history);
        }
        return content;
    }

    /**
     * Gets specific Link.
     *
     * @return with the link as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getLink/{id:.+}")
    public TableDataInformationJson getLinkById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the link
            var id = Long.valueOf(requestedId);
            var person = linkProvider.getLinkAsObject(id, currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(person);
        }
        return content;
    }

    /**
     * Add a One-Time Adoration Link.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/registerOneTimeAdoration")
    public ResponseEntity<String> registerOneTimeAdoration(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            var currentUserInformationJson = currentUserProvider.getUserInformation(session);
            //check authorization
            if (!currentUserInformationJson.isRegisteredAdorator) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                DeleteEntityJson p = g.fromJson(body, DeleteEntityJson.class);
                var updatedObjectId = linkProvider.registerOneTimeAdoration(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot register one-time adoration, please check and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot register one-time adoration - data issue.");
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot register one-time adoration, pls contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at register one-time adoration call", e);
        }
        return result;
    }

    /**
     * Add a One-Time Missing Link.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/registerOneTimeMiss")
    public ResponseEntity<String> registerOneTimeMiss(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            var currentUserInformationJson = currentUserProvider.getUserInformation(session);
            //check authorization
            if (!currentUserInformationJson.isRegisteredAdorator) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, DeleteEntityJson.class);
                var updatedObjectId = linkProvider.registerOneTimeMiss(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot register one-time miss, please check and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot register one-time miss - data issue.");
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot register one-time miss, pls contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at register one-time miss call", e);
        }
        return result;
    }

    /**
     * Remove a One-Time Adoration Link.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/unRegisterOneTimeAdoration")
    public ResponseEntity<String> unRegisterOneTimeAdoration(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            var currentUserInformationJson = currentUserProvider.getUserInformation(session);
            //check authorization
            if (!currentUserInformationJson.isRegisteredAdorator) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, DeleteEntityJson.class);
                var updatedObjectId = linkProvider.unRegisterOneTimeAdoration(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot un-register one-time adoration, please check and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot un-register one-time adoration - data issue.");
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot un-register one-time adoration, pls contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at un-register one-time adoration call", e);
        }
        return result;
    }

    /**
     * Remove a One-Time Missing Link.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/unRegisterOneTimeMiss")
    public ResponseEntity<String> unRegisterOneTimeMiss(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            var currentUserInformationJson = currentUserProvider.getUserInformation(session);
            //check authorization
            if (!currentUserInformationJson.isRegisteredAdorator) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, DeleteEntityJson.class);
                var updatedObjectId = linkProvider.unRegisterOneTimeMiss(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot un-register one-time miss, please check and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot un-register one-time miss - data issue.");
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot un-register one-time miss, pls contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_CREATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at un-register one-time miss call", e);
        }
        return result;
    }

}
