package website.magyar.adoration.web.provider;

import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.business.BusinessWithCoordinator;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Coordinator;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.service.AuthenticatedUser;
import website.magyar.adoration.web.service.FacebookUser;
import website.magyar.adoration.web.service.GoogleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * Class to provide information about the actual user.
 */
@Component
public class CurrentUserProvider {
    private static final String GUEST_NAME_INTRO = "Vend\u00e9g - ";

    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;

    @Autowired
    private BusinessWithCoordinator businessWithCoordinator;

    /**
     * Get information about the actual user.
     *
     * @param httpSession that the user have
     * @return with current user information in json
     */
    public CurrentUserInformationJson getUserInformation(HttpSession httpSession) {
        var currentUserInformationJson = new CurrentUserInformationJson(); //default info - user not logged in

        Authentication authentication = null;
        var securityContext = (SecurityContext) httpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        if (securityContext != null) {
            authentication = securityContext.getAuthentication();
        }
        if (authentication != null) {
            var principal = authentication.getPrincipal();
            if (principal instanceof AuthenticatedUser) {
                var user = (AuthenticatedUser) principal;
                if (user.isSessionValid()) {
                    user.extendSessionTimeout();
                    currentUserInformationJson = getCurrentUserInformation(user);
                } else { //session expired!
                    securityContext.setAuthentication(null); // this cleans up the authentication data technically
                    httpSession.removeAttribute(SPRING_SECURITY_CONTEXT_KEY); // this clean up the session itself
                }
            }
        }
        return currentUserInformationJson;
    }

    private CurrentUserInformationJson getCurrentUserInformation(AuthenticatedUser user) {
        String loggedInUserName;
        String userName;
        Person person;
        Social social;
        var currentUserInformationJson = new CurrentUserInformationJson();
        currentUserInformationJson.isLoggedIn = true;  // if authentication is not null then the person is logged in
        currentUserInformationJson.coordinatorId = -1;
        currentUserInformationJson.isHourlyCoordinator = false;
        currentUserInformationJson.isDailyCoordinator = false;
        person = user.getPerson();
        if (person != null) {
            var coordinator = businessWithCoordinator.getCoordinatorFromPersonId(person.getId());
            currentUserInformationJson.fillIdentifiedPersonFields(person, coordinator);
            loggedInUserName = person.getName();
            userName = loggedInUserName;
        } else { //only Social info we have - person is not identified
            userName = "Anonymous";
            loggedInUserName = GUEST_NAME_INTRO + userName;
            if (user instanceof GoogleUser) {
                userName = user.getSocial().getGoogleUserName();
                loggedInUserName = GUEST_NAME_INTRO + userName;
            }
            if (user instanceof FacebookUser) {
                userName = user.getSocial().getFacebookUserName();
                loggedInUserName = GUEST_NAME_INTRO + userName;
            }
        }
        currentUserInformationJson.socialServiceUsed = user.getServiceName();
        currentUserInformationJson.loggedInUserName = loggedInUserName; //user who logged in via social
        currentUserInformationJson.userName = userName; //user who registered as adorator (his/her name may differ from the username used in Social)
        social = user.getSocial();
        if (social != null) {
            currentUserInformationJson.fillIdentifiedSocialFields(social);
        }
        return currentUserInformationJson;
    }

    /**
     * Gets the name of the actual user.
     *
     * @param authentication is the authentication object
     * @return with the name
     */
    public String getQuickUserName(Authentication authentication) {
        var principal = authentication.getPrincipal();
        String loggedInUserName = "";
        Person person;
        if (principal instanceof AuthenticatedUser) {
            var user = (AuthenticatedUser) principal;
            person = user.getPerson();
            if (person != null) {
                loggedInUserName = person.getName();
            } else {
                loggedInUserName = GUEST_NAME_INTRO + "Anonymous";
                if (principal instanceof GoogleUser) {
                    loggedInUserName = GUEST_NAME_INTRO + user.getSocial().getGoogleUserName();
                }
                if (principal instanceof FacebookUser) {
                    loggedInUserName = GUEST_NAME_INTRO + user.getSocial().getFacebookUserName();
                }
            }
        }
        return loggedInUserName;
    }

    /**
     * Register login event in audit trail.
     *
     * @param httpSession       identifies the user
     * @param oauth2ServiceName identifies the used social service
     */
    public void registerLogin(HttpSession httpSession, final String oauth2ServiceName) {
        var currentUserInformationJson = getUserInformation(httpSession);
        var data = oauth2ServiceName;
        long socialId = 0;
        if (currentUserInformationJson.socialId != null) {
            socialId = currentUserInformationJson.socialId;
        } else {
            data = "Unidentified Social data.";
        }
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(socialId,
                currentUserInformationJson.userName, "Login", "User logged in: " + currentUserInformationJson.userName, data);
        businessWithAuditTrail.saveAuditTrailSafe(auditTrail);
    }

    /**
     * Register logout event in audit trail.
     *
     * @param httpSession identifies the user
     */
    public void registerLogout(HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = getUserInformation(httpSession);
        var data = "";
        long socialId = 0;
        if (currentUserInformationJson.socialId != null) {
            socialId = currentUserInformationJson.socialId;
        } else {
            data = "Unidentified Social data.";
        }
        var auditTrail = businessWithAuditTrail.prepareAuditTrail(socialId,
                currentUserInformationJson.userName, "Logout", "User logged out: " + currentUserInformationJson.userName, data);
        businessWithAuditTrail.saveAuditTrailSafe(auditTrail);
    }
}
