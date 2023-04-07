package website.magyar.adoration.web.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.business.BusinessWithCoordinator;
import website.magyar.adoration.database.business.BusinessWithPerson;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.database.tables.Social;
import website.magyar.adoration.web.i18n.Internationalization;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.service.AuthenticatedUser;
import website.magyar.adoration.web.service.FacebookUser;
import website.magyar.adoration.web.service.GoogleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * Class to provide information about the actual user.
 */
@Component
public class CurrentUserProvider {
    private final Logger logger = LoggerFactory.getLogger(CurrentUserProvider.class);
    private static final Map<String, AuthenticatedUser> sessionCache = new HashMap<>();
    private static final Object o = new Object();

    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;

    @Autowired
    private BusinessWithCoordinator businessWithCoordinator;

    @Autowired
    private Internationalization internationalization;

    @Autowired
    private BusinessWithPerson businessWithPerson;

    public void addSession(String id, AuthenticatedUser user) {
        synchronized (o) {
            if (sessionCache.containsKey(id)) {
                logger.warn("Overwriting existing session!! session: {} user: {}", id, user);
                sessionCache.replace(id, user);
            } else {
                sessionCache.put(id, user);
            }
        }
    }

    /**
     * Checks if the actual session-user pair is the originally registered session-user pair or not.
     * @param httpSession is the session to be checked
     * @param authenticatedUser is the user that tries to use the session
     * @return false if something is not correct
     */
    private boolean checkSession(HttpSession httpSession, AuthenticatedUser authenticatedUser) {
        boolean result = false;
        synchronized (o) {
            String key = httpSession.getId();
            if (sessionCache.containsKey(key)) {
                AuthenticatedUser user = sessionCache.get(key);
                result = user.equals(authenticatedUser);
            } //else unregistered session, which is wrong too
        }
        return result;
    }

    /**
     * Remove the session from the list of active sessions.
     * Must be called on session expiration and on logouts.
     *
     * @param sessionId is the id of the session to be removed
     */
    public void removeSession(String sessionId) {
        synchronized (o) {
            if (sessionCache.containsKey(sessionId)) {
                sessionCache.remove(sessionId);
            } else {
                logger.warn("Trial to remove a not-existing session: {}", sessionId);
            }
        }
    }

    /**
     * Get information about the actual user.
     *
     * @param httpSession that the user have
     * @return with current user information in json
     */
    public CurrentUserInformationJson getUserInformation(HttpSession httpSession) {
        var currentUserInformationJson = new CurrentUserInformationJson(); //default info - user not logged in
        currentUserInformationJson.languageCode = internationalization.detectLanguage(httpSession);
        currentUserInformationJson.fillLanguagePack(internationalization.getLanguagePack(currentUserInformationJson.languageCode));
        Authentication authentication = null;
        var securityContext = (SecurityContext) httpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        if (securityContext != null) {
            authentication = securityContext.getAuthentication();
        }
        if (authentication != null) {
            var principal = authentication.getPrincipal();
            if (principal instanceof AuthenticatedUser) {
                var user = (AuthenticatedUser) principal;
                if (user.isSessionValid() && checkSession(httpSession, user)) {
                    user.extendSessionTimeout();
                    currentUserInformationJson = getCurrentUserInformation(httpSession, user, currentUserInformationJson.languageCode);
                    //logger.info("User found {} principal:{} session: {}", currentUserInformationJson.userName, principal, httpSession.getId());
                } else { //session expired!
                    String sessionID = httpSession.getId();
                    //logger.info("Session expired/invalidated: {}", sessionID);
                    removeSession(sessionID);
                    securityContext.setAuthentication(null); // this cleans up the authentication data technically
                    httpSession.removeAttribute(SPRING_SECURITY_CONTEXT_KEY); // this clean up the session itself
                    httpSession.invalidate(); //and finally truly invalidates the session
                }
            }
        }
        return currentUserInformationJson;
    }

    private CurrentUserInformationJson getCurrentUserInformation(HttpSession httpSession, AuthenticatedUser user, String suggestedLanguageCode) {
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
            internationalization.setLanguage(httpSession, currentUserInformationJson.languageCode);
            currentUserInformationJson.fillLanguagePack(internationalization.getLanguagePack(currentUserInformationJson.languageCode));
            loggedInUserName = person.getName();
            userName = loggedInUserName;
        } else { //only Social info we have - person is not identified
            currentUserInformationJson.languageCode = suggestedLanguageCode; //this shall come from the actual session info
            currentUserInformationJson.fillLanguagePack(internationalization.getLanguagePack(suggestedLanguageCode));
            userName = "Anonymous";
            String guestNameIntro = currentUserInformationJson.getLanguageString("common.guest");
            loggedInUserName = guestNameIntro + userName;
            if (user instanceof GoogleUser) {
                userName = user.getSocial().getGoogleUserName();
                loggedInUserName = guestNameIntro + userName;
            }
            if (user instanceof FacebookUser) {
                userName = user.getSocial().getFacebookUserName();
                loggedInUserName = guestNameIntro + userName;
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
                loggedInUserName = "Guest - Anonymous";
                if (principal instanceof GoogleUser) {
                    loggedInUserName = user.getSocial().getGoogleUserName();
                }
                if (principal instanceof FacebookUser) {
                    loggedInUserName = user.getSocial().getFacebookUserName();
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
    public void logLoginInAuditTrail(HttpSession httpSession, final String oauth2ServiceName) {
        var currentUserInformationJson = getUserInformation(httpSession);
        internationalization.setLanguage(httpSession, currentUserInformationJson.languageCode);
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

    public void setUserLanguageCode(HttpSession httpSession, String languageCode) {
        if (languageCode.contentEquals("hu") || languageCode.contentEquals("en")) {
            //set at person, if logged in
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
                        Person person = user.getPerson();
                        if (person != null) {
                            Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
                            AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(person.getId(),
                                    person.getName(), "Set Language", languageCode, null);
                            auditTrailCollection.add(auditTrail);
                            person.setLanguageCode(languageCode);
                            try {
                                businessWithPerson.updatePerson(person, auditTrailCollection);
                            } catch (Exception e) {
                                logger.warn("Update Person: {} with language code:{} failed.", person.getId(), languageCode);
                            }
                        }
                    }
                }
            }
        //regardless the user is logged in or not set the language in the session
        internationalization.setLanguage(httpSession, languageCode);
        }
    }
}
