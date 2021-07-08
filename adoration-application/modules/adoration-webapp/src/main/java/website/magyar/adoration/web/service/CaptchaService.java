package website.magyar.adoration.web.service;

import org.json.JSONObject;
import org.json.JSONTokener;
import website.magyar.adoration.web.configuration.WebAppConfigurationAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service class for Google Captcha service - on server side.
 */
@Component
public class CaptchaService {
    private static final String SECRET_PARAM = "secret";
    private static final String RESPONSE_PARAM = "response";
    private static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    private WebAppConfigurationAccess webAppConfigurationAccess;

    private JSONObject performRecaptchaSiteVerify(String recaptchaResponseToken)
            throws IOException {
        var url = new URL(SITE_VERIFY_URL);
        var postData = new StringBuilder();
        addParam(postData, SECRET_PARAM, webAppConfigurationAccess.getProperties().getCaptchaSiteSecret());
        addParam(postData, RESPONSE_PARAM, recaptchaResponseToken);

        return postAndParseJSON(url, postData.toString());
    }

    private JSONObject postAndParseJSON(URL url, String postData) throws IOException {
        var urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("charset", StandardCharsets.UTF_8.displayName());
        urlConnection.setRequestProperty("Content-Length", Integer.toString(postData.length()));
        urlConnection.setUseCaches(false);
        urlConnection.getOutputStream().write(postData.getBytes(StandardCharsets.UTF_8));
        JSONTokener jsonTokener = new JSONTokener(urlConnection.getInputStream());
        return new JSONObject(jsonTokener);
    }

    private void addParam(StringBuilder postData, String param, String value) throws UnsupportedEncodingException {
        if (postData.length() != 0) {
            postData.append("&");
        }
        postData.append(String.format("%s=%s",
                URLEncoder.encode(param, StandardCharsets.UTF_8.displayName()),
                URLEncoder.encode(value, StandardCharsets.UTF_8.displayName())));
    }

    /**
     * Verify captcha information.
     *
     * @param captcha received from the browser
     * @return true if captch test passed successfully, otherwise returns with false
     * @throws IOException when issue happens during the validation
     */
    public boolean verifyCaptcha(String captcha) throws IOException {
        boolean success;
        var jsonObject = performRecaptchaSiteVerify(captcha);
        success = jsonObject.getBoolean("success");
        return success;
    }
}
