package website.magyar.adoration.web.controller;

import com.google.gson.Gson;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.PersonInformationJson;
import website.magyar.adoration.web.json.TableDataInformationJson;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.PeopleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Controller for accessing the adorator information.
 */
@Controller
public class AdoratorListController extends ControllerBase {

    private final Logger logger = LoggerFactory.getLogger(AdoratorListController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;

    /**
     * Serves the adorators page.
     *
     * @return the name of the adorators jsp file
     */
    @GetMapping(value = "/adorationSecure/adorationList")
    public String adorators(HttpSession httpSession) {
        if (!isRegisteredAdorator(currentUserProvider, httpSession)) {
            return REDIRECT_TO_HOME;
        }
        return "adoratorList";
    }

    /**
     * Serves the adorator list page - adorator view.
     *
     * @return the name of the adorators jsp file
     */
    @GetMapping(value = "/adorationSecure/adorationListPeople")
    public String adoratorsPeople(HttpSession httpSession) {
        if (!isRegisteredAdorator(currentUserProvider, httpSession)) {
            return REDIRECT_TO_HOME;
        }
        return "adoratorListPeople";
    }

    /**
     * Serves the adorator list page - adorator view.
     *
     * @return the name of the adorators jsp file
     */
    @GetMapping(value = "/adorationSecure/adorationListHours")
    public String adoratorsHOurs(HttpSession httpSession) {
        if (!isRegisteredAdorator(currentUserProvider, httpSession)) {
            return REDIRECT_TO_HOME;
        }
        return "adoratorListHours";
    }

    /**
     * Gets full list of adorators, provided info is depending on the right of the logged in user.
     *
     * @param httpSession session that identifies the user
     * @param filter      optional parameter - actually does nothing
     * @return with list of adorators in Json format
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getAdoratorList")
    public TableDataInformationJson getPersonTable(HttpSession httpSession, @RequestParam("filter") Optional<String> filter) {
        TableDataInformationJson content = null;
        if (isRegisteredAdorator(currentUserProvider, httpSession)) {
            var people = peopleProvider.getAdoratorListAsObject(currentUserProvider.getUserInformation(httpSession), isPrivilegedAdorator(currentUserProvider, httpSession));
            content = new TableDataInformationJson(people);
        }
        return content;
    }

    /**
     * Update an existing Person - by a Coordinator.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/updatePersonByCoo")
    public ResponseEntity<String> updatePersonByCoo(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            //check authorization: user must have right user type
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            if (!currentUserInformationJson.isPrivilegedAdorator) {
                result = buildUnauthorizedActionBodyResult();
            } else {
                //authorization checked, ok
                var g = new Gson();
                var p = g.fromJson(body, PersonInformationJson.class);
                var updateInformation = peopleProvider.updatePersonByCoo(p, currentUserInformationJson);
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

}

