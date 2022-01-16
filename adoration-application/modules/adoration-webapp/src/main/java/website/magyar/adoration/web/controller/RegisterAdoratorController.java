package website.magyar.adoration.web.controller;

import com.google.gson.Gson;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.json.RegisterAdoratorJson;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Controller for registering a new adorator.
 */
@Controller
public class RegisterAdoratorController extends ControllerBase {

    private final Logger logger = LoggerFactory.getLogger(RegisterAdoratorController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;

    /**
     * Serves the adorRegistration page.
     *
     * @return the name of the adorRegistration jsp file
     */
    @GetMapping(value = "/adoration/adorRegistration")
    public String adorRegistration(HttpSession httpSession) {
        currentUserProvider.getUserInformation(httpSession);
        return "adorRegistration";
    }

    /**
     * Register a new adorator.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adoration/registerAdorator")
    public ResponseEntity<String> registerAdorator(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            var currentUserInformationJson = currentUserProvider.getUserInformation(session);
            var g = new Gson();
            var p = g.fromJson(body, RegisterAdoratorJson.class);
            p.personId = currentUserInformationJson.personId;
            p.socialId = currentUserInformationJson.socialId;
            p.languageCode = currentUserInformationJson.languageCode;
            //authorization is irrelevant
            var updateInformation = peopleProvider.registerAdorator(p, currentUserInformationJson.userName);
            if (updateInformation != null) {
                resultString = "OK-" + updateInformation.toString();
                result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.CREATED);
            } else {
                resultString = currentUserInformationJson.getLanguageString("adorRegistration.Failed");
                result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
                logger.info("Cannot register Adorator: {}", p.name);
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Sorry and error occurred - please contact to site maintainers!";
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at register new Adorator function, pls contact to maintainers", e);
        }
        return result;
    }

    /**
     * Called when the registration of a new adorator was successful.
     *
     * @return with the correct page content
     */
    @GetMapping(value = "/adoration/registrationSuccess")
    public String registrationSuccess(HttpSession httpSession) {
        currentUserProvider.getUserInformation(httpSession);
        return "registrationSuccess";
    }

}
