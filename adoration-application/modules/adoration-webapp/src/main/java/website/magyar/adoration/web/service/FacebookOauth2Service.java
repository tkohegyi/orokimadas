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
    private static final String SUBJECT = "[AdoratorApp] - ??j Facebook Social";

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
                    + "!\n\nK??sz??nettel vett??k els?? bejelentkez??sedet a V??ci ??r??kim??d??s (https://orokimadas.info:9092/) weboldal??n."
                    + "\n\nA k??vetkez?? adatokat ismert??k meg r??lad:"
                    + "\nN??v: " + social.getFacebookUserName()
                    + "\nE-mail: " + social.getFacebookEmail()
                    + "\nFacebook azonos??t??: " + social.getFacebookUserId()
                    + "\n\nAdatkezel??si t??j??koztat??nkat megtal??lhatod itt: https://orokimadas.info:9092/resources/img/AdatkezelesiSzabalyzat.pdf"
                    + "\nAdataidr??l inform??ci??t illetve azok t??rl??s??t pedig erre az e-mail c??mre ??rva k??rheted: kohegyi.tamas (kukac) vac-deakvar.vaciegyhazmegye.hu."
                    + "\nUgyanezen a c??men v??rjuk leveledet akkor is, ha k??rd??sed, ??szrev??teled vagy javaslatod van a weboldallal kapcsolatban. ";
            if (personDetected) {
                text += "\n\nMivel m??r regiszt??lva vagy, b??rtan haszn??ld a weboldal szolg??ltat??sait.";

            } else { //person not detected
                text += "\n\nMivel olyan e-mail c??met haszn??lt??l, amely alapj??n nem tudjuk pontosan, hogy ki vagy, ez??rt "
                        + "erre a lev??lre v??laszolva k??rlek ??rd meg, hogy ki vagy ??s mikor szokt??l az ??r??kim??d??sban r??szt venni, "
                        + "vagy a telefonsz??modat, hogy felvehess??k veled a kapcsolatot. Ez mindenf??lek??ppen sz??ks??ges, hogy a megfelel?? azonos??t??s megt??rt??njen."
                        + " Am??g ez nem t??rt??nik meg, csak korl??tozottan tudunk hozz??f??r??st biztos??tani a weboldalhoz.";
            }
            text += "\n\n??dv??zlettel:\nK??hegyi Tam??s\naz ??r??kim??d??s vil??gi koordin??tora\n+36-70-375-4140\n";
            //send feedback mail to the registered user
            emailSender.sendMailFromSocialLogin(social.getFacebookEmail(), "Bel??p??s az ??r??kim??d??s weboldal??n Facebook azonos??t??val", text);
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
