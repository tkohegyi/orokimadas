package website.magyar.adoration.web.controller;

import com.google.gson.Gson;
import website.magyar.adoration.database.tables.Link;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.DeleteEntityJson;
import website.magyar.adoration.web.json.PersonInformationJson;
import website.magyar.adoration.web.json.TableDataInformationJson;
import website.magyar.adoration.web.provider.CoverageProvider;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.PeopleProvider;
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
 * Controller for accessing the application log files.
 */
@Controller
public class AdoratorsController extends ControllerBase {

    private final Logger logger = LoggerFactory.getLogger(AdoratorsController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;
    @Autowired
    private CoverageProvider coverageProvider;

    /**
     * Serves the adorators page.
     *
     * @return the name of the adorators jsp file
     */
    @GetMapping(value = "/adorationSecure/adorators")
    public String adorators(HttpSession httpSession) {
        if (!isAdoratorAdmin(currentUserProvider, httpSession)) {
            return REDIRECT_TO_HOME;
        }
        return "adorators";
    }

    /**
     * Gets the list of Adorators.
     *
     * @return with the list of people as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getPersonTable")
    public TableDataInformationJson getPersonTable(HttpSession httpSession, @RequestParam("filter") Optional<String> filter) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person table
            var people = peopleProvider.getPersonListAsObject(); // this says [{"id":372,"name" we need data in head
            content = new TableDataInformationJson(people);
        }
        return content;
    }

    /**
     * Gets specific Adorator.
     *
     * @return with the person as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getPerson/{id:.+}")
    public TableDataInformationJson getPersonById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isPrivilegedAdorator(currentUserProvider, httpSession)) {
            //can get the person
            try {
                var id = Long.valueOf(requestedId);
                var person = peopleProvider.getPersonAsObject(id);
                content = new TableDataInformationJson(person);
            } catch (NumberFormatException e) {
                logger.warn("Rouge request to getPerson endpoint with bad id.");
            }
        }
        return content;
    }

    /**
     * Gets log history of a specific Adorator.
     *
     * @return with the person history as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getPersonHistory/{id:.+}")
    public TableDataInformationJson getPersonHistoryById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person history
            try {
                var id = Long.valueOf(requestedId);
                var personHistory = peopleProvider.getPersonHistoryAsObject(id);
                content = new TableDataInformationJson(personHistory);
            } catch (NumberFormatException e) {
                logger.warn("Rouge request to getPersonHistory endpoint with bad id.");
            }
        }
        return content;
    }

    /**
     * Gets hour assignments of a specific Adorator.
     *
     * @return with the hour assignments of a person as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getPersonCommitments/{id:.+}")
    public TableDataInformationJson getPersonCommitmentsById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person commitments
            try {
                var id = Long.valueOf(requestedId);
                var personCommitments = coverageProvider.getPersonCommitmentAsObject(id, currentUserProvider.getUserInformation(httpSession));
                content = new TableDataInformationJson(personCommitments);
            } catch (NumberFormatException e) {
                logger.warn("Rouge request to getPersonCommitments endpoint with bad id.");
            }
        }
        return content;
    }

    /**
     * Update an existing Person.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/updatePerson")
    public ResponseEntity<String> updatePerson(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            //check authorization: user must have right user type
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            if (!currentUserInformationJson.isAdoratorAdmin) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, PersonInformationJson.class);
                var updateInformation = peopleProvider.updatePerson(p, currentUserInformationJson);
                if (updateInformation != null) {
                    resultString = "OK-" + updateInformation.toString();
                    result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot update the Person, please check the values and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot update the Person with ID: {}", p.id);
                }
            }
        } catch (SystemException e) {
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot update the Person, please contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at update Person, pls contact to maintainers", e);
        }
        return result;
    }

    /**
     * Update commitments of an existing Person.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/updatePersonCommitment")
    public ResponseEntity<String> updatePersonCommitment(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            //check authorization: user must have right user type
            if (!currentUserInformationJson.isAdoratorAdmin) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, Link.class);
                var updateInformation = coverageProvider.updatePersonCommitment(p, currentUserInformationJson);
                if (updateInformation != null) {
                    resultString = "OK-" + updateInformation.toString();
                    result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot update the Person Commitment, please check the values and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot update the Person Commitment with ID: {}", p.getId());
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot update the Person Commitment, please contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at PersonCommitment, pls contact to maintainers", e);
        }
        return result;
    }

    /**
     * Delete an existing Person.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/deletePerson")
    public ResponseEntity<String> deletePerson(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            //check authorization
            if (!currentUserInformationJson.isAdoratorAdmin) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, DeleteEntityJson.class);
                var updatedObjectId = peopleProvider.deletePerson(p);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot delete Person, please check and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot delete Person - data issue.");
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot delete Person, pls contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at delete Person call", e);
        }
        return result;
    }

    /**
     * Delete an existing Link.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/deletePersonCommitment")
    public ResponseEntity<String> deletePersonCommitment(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            //check authorization
            if (!currentUserInformationJson.isAdoratorAdmin) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, DeleteEntityJson.class);
                var updatedObjectId = coverageProvider.deletePersonCommitment(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot delete Person Commitment, please check and retry.";
                    result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
                    logger.info("Cannot delete Link - data issue.");
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot delete Person Commitment, pls contact to maintainers.";
            result = buildResponseBodyResult(JSON_RESPONSE_DELETE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at delete Person Commitment call", e);
        }
        return result;
    }

}
