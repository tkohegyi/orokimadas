package website.magyar.adoration.web.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.business.BusinessWithNextGeneralKey;
import website.magyar.adoration.database.business.BusinessWithPerson;
import website.magyar.adoration.database.business.BusinessWithSocial;
import website.magyar.adoration.database.business.helper.enums.SocialStatusTypes;
import website.magyar.adoration.database.json.GoogleUserInfoJson;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;
import website.magyar.adoration.helper.EmailSender;
import website.magyar.adoration.web.configuration.WebAppConfigurationAccess;
import website.magyar.adoration.web.service.helper.Oauth2ServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * For Handling proper Google Oauth2 authorization tasks.
 */
@Component
public class GoogleOauth2Service extends Oauth2ServiceBase {

    public static final String GOOGLE_TEXT = "Google";

    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final String SUBJECT = "[AdoratorApp] - Új Google Social";
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");

    private final Logger logger = LoggerFactory.getLogger(GoogleOauth2Service.class);
    private final HttpTransport httpTransport = new NetHttpTransport();

    private GoogleAuthorizationCodeFlow flow;

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

    @PostConstruct
    private void googleOauth2Service() {
        var propertyDto = webAppConfigurationAccess.getProperties();
        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
                new JacksonFactory(), propertyDto.getGoogleClientId(), propertyDto.getGoogleClientSecret(), SCOPES).build();
    }

    /**
     * Gets the Google login url.
     *
     * @return with Google login url
     */
    public String getLoginUrlInformation() {
        //see help from https://www.programcreek.com/java-api-examples/?api=com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
        //see help from https://www.programcreek.com/java-api-examples/index.php?api=com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl

        var installedDetails = new GoogleClientSecrets.Details();
        var propertyDto = webAppConfigurationAccess.getProperties();
        installedDetails.setClientId(propertyDto.getGoogleClientId());
        installedDetails.setClientSecret(propertyDto.getGoogleClientSecret());

        var clientSecrets = new GoogleClientSecrets();
        clientSecrets.setInstalled(installedDetails);

        var googleAuthorizationCodeRequestUrl = new GoogleAuthorizationCodeRequestUrl(clientSecrets, propertyDto.getGoogleRedirectUrl(), SCOPES);
        return googleAuthorizationCodeRequestUrl.build();
    }

    /**
     * Beware that this can be NULL, if something is wrong.
     *
     * @param authCode authenticaton code received from Google
     * @return with Spring Authentication object
     */
    public Authentication getGoogleUserInfoJson(final String authCode) {
        Authentication authentication = null;
        var propertyDto = webAppConfigurationAccess.getProperties();
        try {
            GoogleUser googleUser;
            final GoogleTokenResponse response = flow.newTokenRequest(authCode)
                    .setRedirectUri(propertyDto.getGoogleRedirectUrl())
                    .execute();
            final Credential credential = flow.createAndStoreCredential(response, null);
            final HttpRequest request = httpTransport.createRequestFactory(credential)
                    .buildGetRequest(new GenericUrl(USER_INFO_URL));
            request.getHeaders().setContentType("application/json");

            var gson = new Gson();
            var googleUserInfoJson = gson.fromJson(request.execute().parseAsString(), GoogleUserInfoJson.class);

            var social = detectSocial(googleUserInfoJson);
            var person = detectPerson(social);
            googleUser = new GoogleUser(social, person, propertyDto.getSessionTimeout());

            //googleUser used as Principal, credential is coming from Google
            authentication = adorationCustomAuthenticationProvider.authenticate(new PreAuthenticatedAuthenticationToken(googleUser, credential));
        } catch (Exception ex) {
            logger.warn("Was unable to get Google User Information.", ex);
        }
        return authentication;
    }

    private Person detectPerson(Social social) {
        Person person = null;
        var personId = social.getPersonId();
        if (personId != null) {
            person = businessWithPerson.getPersonById(personId);
        }
        return person;
    }

    private Social detectSocial(GoogleUserInfoJson googleUserInfoJson) {
        googleUserInfoJson.email = makeEmptyStringFromNull(googleUserInfoJson.email);
        googleUserInfoJson.name = makeEmptyStringFromNull(googleUserInfoJson.name);
        googleUserInfoJson.picture = makeEmptyStringFromNull(googleUserInfoJson.picture);
        var social = businessWithSocial.getSocialByGoogleUserId(googleUserInfoJson.id); //if there is no social this will cause exception that is unhandled !!!
        if (social == null) {
            boolean personDetected = false;
            social = new Social();
            social.setGoogleEmail(googleUserInfoJson.email);
            social.setGoogleUserName(googleUserInfoJson.name);
            social.setGoogleUserId(googleUserInfoJson.id);
            social.setGoogleUserPicture(googleUserInfoJson.picture);
            social.setSocialStatus(SocialStatusTypes.WAIT_FOR_IDENTIFICATION.getTypeValue());
            var id = businessWithNextGeneralKey.getNextGeneralId();
            social.setId(id);
            var auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getGoogleUserName(),
                    AUDIT_SOCIAL_CREATE + id.toString(), "New Google Social login created.", GOOGLE_TEXT);
            //this is a brand new login, try to identify - by using e-mail
            if ((googleUserInfoJson.email != null) && (googleUserInfoJson.email.length() > 0)) {
                var p = businessWithPerson.getPersonByEmail(googleUserInfoJson.email);
                if (p != null) { // we were able to identify the person by e-mail
                    social.setPersonId(p.getId());
                    social.setSocialStatus(SocialStatusTypes.IDENTIFIED_USER.getTypeValue());
                    personDetected = true;
                }
            }
            var text = "New Social id: " + id.toString() + "\nGoogle Type,\nName: " + social.getGoogleUserName() + ",\nEmail: " + social.getGoogleEmail();
            emailSender.sendMailToAdministrator(SUBJECT, text); //to administrator to inform about the person
            text = "Kedves " + social.getGoogleUserName()
                    + "!\n\nKöszönettel vettük első bejelentkezésedet a Váci Örökimádás (https://orokimadas.info:9092/) weboldalán."
                    + "\n\nA következő adatokat ismertük meg rólad:"
                    + "\nNév: " + social.getGoogleUserName()
                    + "\nE-mail: " + social.getGoogleEmail()
                    + "\nGoogle azonosító: " + social.getGoogleUserId()
                    + "\nGoogle kép: " + social.getGoogleUserPicture()
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
            emailSender.sendMailFromSocialLogin(social.getGoogleEmail(), "Belépés az Örökimádás weboldalán Google azonosítóval", text);
            id = businessWithSocial.newSocial(social, auditTrail);
            social.setId(id); //Social object is ready
        } else {
            //detect social update and act
            autoUpdateGoogleSocial(social, googleUserInfoJson.name, googleUserInfoJson.email, googleUserInfoJson.picture);
        }
        return social;
    }

    private void autoUpdateGoogleSocial(Social social, String name, String email, String picture) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        if (social.getGoogleUserName().compareToIgnoreCase(name) != 0) {
            social.setGoogleUserName(name);
            var id = businessWithNextGeneralKey.getNextGeneralId();
            var auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getGoogleUserName(), AUDIT_SOCIAL_UPDATE + social.getId().toString(),
                    "Google Username updated to:" + name, GOOGLE_TEXT);
            auditTrailCollection.add(auditTrail);
        }
        if (social.getGoogleEmail().compareToIgnoreCase(email) != 0) {
            social.setGoogleEmail(email);
            var id = businessWithNextGeneralKey.getNextGeneralId();
            var auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getGoogleUserName(), AUDIT_SOCIAL_UPDATE + social.getId().toString(),
                    "Google Email updated to:" + email, GOOGLE_TEXT);
            auditTrailCollection.add(auditTrail);
        }
        if (social.getGoogleUserPicture().compareToIgnoreCase(picture) != 0) {
            social.setGoogleUserPicture(picture);
            var id = businessWithNextGeneralKey.getNextGeneralId();
            var auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getGoogleUserName(), AUDIT_SOCIAL_UPDATE + social.getId().toString(),
                    "Google Picture updated to:" + picture, GOOGLE_TEXT);
            auditTrailCollection.add(auditTrail);
        }
        if (!auditTrailCollection.isEmpty()) {
            businessWithSocial.updateSocial(social, auditTrailCollection);
        }
    }

}
