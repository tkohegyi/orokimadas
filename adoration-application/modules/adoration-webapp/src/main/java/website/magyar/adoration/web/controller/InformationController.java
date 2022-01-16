package website.magyar.adoration.web.controller;

import com.google.gson.Gson;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.MessageToCoordinatorJson;
import website.magyar.adoration.web.json.TableDataInformationJson;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.InformationProvider;
import website.magyar.adoration.web.provider.PeopleProvider;
import website.magyar.adoration.web.service.CaptchaService;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller for handling requests for the application pages about Information.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class InformationController extends ControllerBase {
    private final Logger logger = LoggerFactory.getLogger(InformationController.class);
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private InformationProvider informationProvider;
    @Autowired
    private PeopleProvider peopleProvider;
    @Autowired
    private CaptchaService captchaService;

    /**
     * Serves user requests for general Information.
     *
     * @return the name of the jsp to display as result
     */
    @GetMapping(value = "/adorationSecure/information")
    public String informationPage(HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        if (currentUserInformationJson.isRegisteredAdorator) { //registered adorator
            return "information";
        }
        if (currentUserInformationJson.isLoggedIn) { //can be waiting for identification or guest
            return "guestinfo";
        }
        return REDIRECT_TO_HOME; //not even logged in -> go back to basic home page
    }

    /**
     * Serves general information data for the logged in user, who is an adorator (at least).
     *
     * @param httpSession         identifies the user
     * @param httpServletResponse is used to build the response
     * @return with proper content
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getInformation")
    public TableDataInformationJson getInformation(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        TableDataInformationJson content = null;
        if (isRegisteredAdorator(currentUserProvider, httpSession)) {
            //has right to collect and see information
            var information = informationProvider.getInformation(currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(information);
        }
        return content;
    }

    /**
     * Serves general information for a logged-in user (who is not-yet identified, or identified as Guest).
     *
     * @param httpSession         identifies the user
     * @param httpServletResponse is used to build the response
     * @return with proper content
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getGuestInformation")
    public TableDataInformationJson getProfileInformation(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        TableDataInformationJson content = null;
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        if (currentUserInformationJson.isRegisteredAdorator) { //registered adorator
            //we should not be here, this area for Guests only
            logger.warn("Registered adorator: {} reached hidden area, pls check!", currentUserInformationJson.personId);
            content = new TableDataInformationJson(null);
        } else if (currentUserInformationJson.isLoggedIn) { //can be waiting for identification or guest
            var information = informationProvider.getGuestInformation(currentUserInformationJson);
            content = new TableDataInformationJson(information);
        }
        return content;
    }

    /**
     * Serves requests to send message to main coordinator(s).
     *
     * @return with info about the result of the message sending
     */
    @ResponseBody
    @PostMapping(value = "/adorationSecure/messageToCoordinator")
    public ResponseEntity<String> messageToCoordinator(@RequestBody final String body, final HttpSession httpSession) {
        String resultString;
        ResponseEntity<String> result;
        try {
            var currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
            var g = new Gson();
            var p = g.fromJson(body, MessageToCoordinatorJson.class);
            //authorization is irrelevant, just the login status
            if (currentUserInformationJson.isLoggedIn && isCaptchaValid(p.captcha)) { //anybody who logged in can send message to maintainers
                peopleProvider.messageToCoordinator(p, currentUserInformationJson);
                resultString = "OK";
                result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.CREATED);
            } else { //a non logged in person or without proper captcha, a user wants to send something - it is prohibited
                resultString = currentUserInformationJson.getLanguageString("messageToCoordinator.Failed");
                result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
                logger.warn("WARNING, somebody - who was not logged in or did not set proper captcha - tried to send a message to us.");
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Az üzenetküldés sikertelen, kérjük lépjen kapcsolatba a weboldal karbantartójával!";
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at send message to coordinator function, pls contact to maintainers", e);
        }
        return result;
    }

    private boolean isCaptchaValid(String captcha) {
        if ((captcha != null) && (captcha.length() > 0)) {
            //we have something to be checked
            try {
                return captchaService.verifyCaptcha(captcha);
            } catch (Exception e) {
                logger.warn("Issue at calling Google reCaptcha service", e);
                return true; //we should not punish our user in this case
            }
        }
        return false;
    }

}
