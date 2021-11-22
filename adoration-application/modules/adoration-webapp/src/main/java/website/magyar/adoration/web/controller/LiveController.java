package website.magyar.adoration.web.controller;

import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.LiveAdoratorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the online adoration.
 */
@Controller
public class LiveController extends ControllerBase {
    private static final String JSON_INFO = "hash";

    @Autowired
    private LiveAdoratorProvider liveAdoratorProvider;
    @Autowired
    private CurrentUserProvider currentUserProvider;

    /**
     * Serves the live adoration page.
     *
     * @return with proper content
     */
    @GetMapping(value = "/adorationSecure/live")
    public String live(HttpSession httpSession) {
        currentUserProvider.getUserInformation(httpSession);
        return "live";
    }

    /**
     * Registers actual user in actual live list of online adorators.
     *
     * @param httpSession         identifies the user
     * @param httpServletResponse is used to build up the response
     * @return with hash information to be used by the browser as heartbeat
     */
    @ResponseBody
    @GetMapping(value = "/adoration/registerLiveAdorator")
    public Map<String, Collection<String>> registerLiveAdorator(HttpSession httpSession, HttpServletResponse httpServletResponse) {

        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");

        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        var hash = liveAdoratorProvider.registerLiveAdorator(currentUserInformationJson);

        jsonString.add(hash);
        jsonResponse.put(JSON_INFO, jsonString);
        return jsonResponse;
    }

    /**
     * Recognises heartbeat from live adorator.
     *
     * @param hashString is the adorator identifier
     * @return nothing special
     */
    @GetMapping(value = "/adoration/liveAdorator/{hash:.+}")
    public ResponseEntity<String> liveAdoratorHeartBeat(HttpSession httpSession, @PathVariable("hash") final String hashString) {
        currentUserProvider.getUserInformation(httpSession); //keep session alive even if user does nothing - after all, the user is adorating
        liveAdoratorProvider.incomingTick(hashString);
        var jsonData = "{\"hash\":\"" + hashString + "\"}";
        var responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(jsonData, responseHeaders, HttpStatus.OK);
    }

}
