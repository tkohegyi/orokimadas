package website.magyar.adoration.web.service;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.business.BusinessWithNextGeneralKey;
import website.magyar.adoration.database.business.BusinessWithPerson;
import website.magyar.adoration.database.business.BusinessWithSocial;
import website.magyar.adoration.database.business.helper.enums.SocialStatusTypes;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.helper.EmailSender;
import website.magyar.adoration.web.configuration.PropertyDto;
import website.magyar.adoration.web.configuration.WebAppConfigurationAccess;
import website.magyar.adoration.web.service.helper.Oauth2ServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For Handling proper Facebook Oauth2 authorization tasks.
 */
@Component
public class FacebookOauth2Service extends Oauth2ServiceBase {

    public static final String FACEBOOK_TEXT = "Facebook";

    private static final String GRAPH_URL = "https://graph.facebook.com/v13.0/oauth/access_token?";
    private static final String AUTHORIZATION_URL = "https://www.facebook.com/v13.0/dialog/oauth?";
    private static final String SUBJECT = "[AdoratorApp] - Új Facebook Social";

    private final Logger logger = LoggerFactory.getLogger(FacebookOauth2Service.class);

    @Autowired
    private AdorationCustomAuthenticationProvider adorationCustomAuthenticationProvider;
    @Autowired
    private WebAppConfigurationAccess webAppConfigurationAccess;
    @Autowired
    private BusinessWithSocial businessWithSocial;
    @Autowired
    private BusinessWithPerson businessWithPerson;
    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    private BusinessWithNextGeneralKey businessWithNextGeneralKey;
    @Autowired
    private EmailSender emailSender;

    private FacebookConnectionFactory facebookConnectionFactory;

    @PostConstruct
    private void facebookOauth2Service() {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        facebookConnectionFactory = new FacebookConnectionFactory(propertyDto.getFacebookAppId(), propertyDto.getFacebookAppSecret());
        facebookConnectionFactory.setScope("email,public_profile");
    }

    /**
     * Get Facebook redirect URL to its Oauth2 service.
     *
     * @return with the Url
     */
    public String getLoginUrlInformation() {
        var propertyDto = webAppConfigurationAccess.getProperties();
        String authorizationUrl;
        authorizationUrl = AUTHORIZATION_URL
                + "client_id=" + propertyDto.getFacebookAppId()
                + "&redirect_uri=" + propertyDto.getGoogleRedirectUrl()
                + "&state=no-state&display=popup&response_type=code&scope=" + facebookConnectionFactory.getScope();
        //note use this: &response_type=granted_scopes to get list of granted scopes
        return authorizationUrl;
    }

    private String getFacebookGraphUrl(final String code, final String applicationId,
                                       final String applicationSecret, final String redirectUrl) throws UnsupportedEncodingException {
        String fbGraphUrl;
        fbGraphUrl = GRAPH_URL
                + "client_id=" + applicationId
                + "&redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8")
                + "&client_secret=" + applicationSecret
                + "&code=" + code;
        return fbGraphUrl;
    }

    private String getAccessToken(String code, String applicationId, String applicationSecret, String redirectUrl) throws IOException, ParseException {
        URL fbGraphURL;
        var fbGraphURLString = getFacebookGraphUrl(code, applicationId, applicationSecret, redirectUrl);
        fbGraphURL = new URL(fbGraphURLString);
        var fbConnection = fbGraphURL.openConnection(); //NOSONAR - code is properly protected
        var in = new BufferedReader(new InputStreamReader(fbConnection.getInputStream()));
        String inputLine;
        var b = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            b.append(inputLine + "\n");
        }
        in.close();

        var accessToken = b.toString();
        var parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        var json = (JSONObject) parser.parse(accessToken);
        accessToken = json.getAsString("access_token");
        return accessToken;
    }

    private JSONObject getFacebookGraph(String accessToken) {
        String graph;
        JSONObject json;
        try {
            var g = "https://graph.facebook.com/me?access_token=" + accessToken + "&fields=name,id,email";
            var u = new URL(g);
            var c = u.openConnection();
            var in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            var b = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                b.append(inputLine + "\n");
            }
            in.close();
            graph = b.toString();
            var parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            json = (JSONObject) parser.parse(graph);
        } catch (Exception e) {
            throw new SystemException("ERROR in getting FB graph data. ", e);
        }
        return json;
    }

    /**
     * Authenticate the user with Facebook Oauth2 service.
     *
     * @param authCode is the code arrived from Facebook
     * @return with the Authentication class (with a Facebook user in it) or null
     */
    public Authentication getFacebookUserInfoJson(final String authCode) {
        FacebookUser facebookUser;
        Authentication authentication = null;
        var propertyDto = webAppConfigurationAccess.getProperties();
        try {
            var accessToken = getAccessToken(authCode, propertyDto.getFacebookAppId(), propertyDto.getFacebookAppSecret(), propertyDto.getGoogleRedirectUrl());
            var facebookUserInfoJson = getFacebookGraph(accessToken);
            var social = detectSocial(facebookUserInfoJson);
            var person = detectPerson(social);
            facebookUser = new FacebookUser(social, person, propertyDto.getSessionTimeout());

            // googleUser used as Principal, credential is coming from Google
            authentication = adorationCustomAuthenticationProvider.authenticate(new PreAuthenticatedAuthenticationToken(facebookUser, facebookUserInfoJson));
        } catch (Exception e) {
            logger.warn("Was unable to get Facebook User Information.", e);
        }
        return authentication;
    }

    private Person detectPerson(Social social) {
        Person person = null;
        Long personId = social.getPersonId();
        if (personId != null) {
            person = businessWithPerson.getPersonById(personId);
        }
        return person;
    }

    private Social detectSocial(JSONObject facebookUserInfoJson) {
        var userId = facebookUserInfoJson.getAsString("id");
        var email = facebookUserInfoJson.getAsString("email");
        email = makeEmptyStringFromNull(email);
        var firstName = facebookUserInfoJson.getAsString("name");
        firstName = makeEmptyStringFromNull(firstName);
        var social = businessWithSocial.getSocialByFacebookUserId(userId);
        if (social == null) {
            boolean personDetected = false;
            social = new Social();
            social.setFacebookUserId(userId);
            social.setFacebookEmail(email);
            social.setFacebookFirstName(firstName);
            social.setFacebookUserName(social.getFacebookFirstName());  // this is what we can access by default...
            social.setSocialStatus(SocialStatusTypes.WAIT_FOR_IDENTIFICATION.getTypeValue());
            var id = businessWithNextGeneralKey.getNextGeneralId();
            social.setId(id);
            var auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getFacebookUserName(),
                    AUDIT_SOCIAL_CREATE + id.toString(), "New Facebook Social login created.", FACEBOOK_TEXT);
            //this is a brand new login, try to identify - by using e-mail
            if ((email != null) && (email.length() > 0)) {
                var p = businessWithPerson.getPersonByEmail(email);
                if (p != null) { // we were able to identify the person by e-mail
                    social.setPersonId(p.getId());
                    social.setSocialStatus(SocialStatusTypes.IDENTIFIED_USER.getTypeValue());
                    personDetected = true;
                }
            }
            var text = "New Social id: " + id.toString() + "\nFacebook Type,\n Name: " + social.getFacebookUserName() + ",\nEmail: " + social.getFacebookEmail();
            emailSender.sendMailToAdministrator(SUBJECT, text); //send mail to administrator
            text = "Kedves " + social.getFacebookUserName()
                    + "!\n\nKöszönettel vettük első bejelentkezésedet a Váci Örökimádás (https://orokimadas.info:9092/) weboldalán."
                    + "\n\nA következő adatokat ismertük meg rólad:"
                    + "\nNév: " + social.getFacebookUserName()
                    + "\nE-mail: " + social.getFacebookEmail()
                    + "\nFacebook azonosító: " + social.getFacebookUserId()
                    + "\n\nAdatkezelési tájékoztatónkat megtalálhatod itt: https://orokimadas.info:9092/resources/img/AdatkezelesiSzabalyzat.pdf"
                    + "\nAdataidról információt illetve azok törlését pedig erre az e-mail címre írva kérheted: kohegyi.tamas (kukac) vac-deakvar.vaciegyhazmegye.hu."
                    + "\nUgyanezen a címen várjuk leveledet akkor is, ha kérdésed, észrevételed vagy javaslatod van a weboldallal kapcsolatban. ";
            if (personDetected) {
                text += "\n\nMivel már regisztálva vagy, bártan használd a weboldal szolgáltatásait.";

            } else { //person not detected
                text += "\n\nMivel olyan e-mail címet használtál, amely alapján nem tudjuk pontosan, hogy ki vagy, ezért "
                        + "erre a levélre válaszolva kérlek írd meg, hogy ki vagy és mikor szoktál az Örökimádásban részt venni, "
                        + "vagy a telefonszámodat, hogy felvehessük veled a kapcsolatot. Ez mindenféleképpen szükséges, hogy a megfelelő azonosítás megtörténjen."
                        + " Amíg ez nem történik meg, csak korlátozottan tudunk hozzáférést biztosítani a weboldalhoz.";
            }
            text += "\n\nÜdvözlettel:\nKőhegyi Tamás\naz örökimádás világi koordinátora\n+36-70-375-4140\n";
            //send feedback mail to the registered user
            emailSender.sendMailFromSocialLogin(social.getFacebookEmail(), "Belépés az Örökimádás weboldalán Facebook azonosítóval", text);
            id = businessWithSocial.newSocial(social, auditTrail);
            social.setId(id); //Social object is ready
        } else {
            //detect social update and act
            autoUpdateFacebookSocial(social, firstName, email);
        }
        return social;
    }

    private void autoUpdateFacebookSocial(Social social, String firstName, String email) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        if (social.getFacebookFirstName().compareToIgnoreCase(firstName) != 0) {
            social.setFacebookFirstName(firstName);
            social.setFacebookUserName(firstName);  // this is what we can access by default...
            var id = businessWithNextGeneralKey.getNextGeneralId();
            AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getFacebookUserName(), AUDIT_SOCIAL_UPDATE + social.getId().toString(),
                    "Facebook FirstName/Name updated to:" + firstName, FACEBOOK_TEXT);
            auditTrailCollection.add(auditTrail);
        }
        if (social.getFacebookEmail().compareToIgnoreCase(email) != 0) {
            social.setFacebookEmail(email);
            var id = businessWithNextGeneralKey.getNextGeneralId();
            AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getFacebookUserName(), AUDIT_SOCIAL_UPDATE + social.getId().toString(),
                    "Facebook Email updated to:" + email, FACEBOOK_TEXT);
            auditTrailCollection.add(auditTrail);
        }
        if (!auditTrailCollection.isEmpty()) {
            businessWithSocial.updateSocial(social, auditTrailCollection);
        }
    }

}
