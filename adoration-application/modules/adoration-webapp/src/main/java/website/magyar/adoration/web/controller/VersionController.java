package website.magyar.adoration.web.controller;

import website.magyar.adoration.configuration.VersionTitleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for version information.
 */
@Controller
public class VersionController {

    @Autowired
    private VersionTitleProvider titleProvider;

    /**
     * Returns the build version of the application as a JSON response.
     *
     * @return the JSON response containing the build version
     */
    @ResponseBody
    @GetMapping(value = "/version")
    public ResponseEntity<String> getVersion() {
        var adorAppVersion = titleProvider.getVersionTitle();
        var jsonData = "{\"adorationApplicationVersion\":\"" + adorAppVersion + "\"}";
        var responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(jsonData, responseHeaders, HttpStatus.OK);
    }
}
