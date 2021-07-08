package website.magyar.adoration.web.provider;

import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.business.BusinessWithSocial;
import website.magyar.adoration.database.business.helper.enums.SocialStatusTypes;
import website.magyar.adoration.database.exception.DatabaseHandlingException;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Social;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.DeleteEntityJson;
import website.magyar.adoration.web.provider.helper.ProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to provide information about social users.
 */
@Component
public class SocialProvider extends ProviderBase {

    private final Logger logger = LoggerFactory.getLogger(SocialProvider.class);

    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    private BusinessWithSocial businessWithSocial;

    /**
     * Get simple full list of social logins.
     *
     * @return with the list as object
     */
    public Object getSocialListAsObject() {
        return businessWithSocial.getSocialList();
    }

    /**
     * Get a specific social login.
     *
     * @param id of the social login
     * @return with the social record as object
     */
    public Object getSocialAsObject(final Long id) {
        return businessWithSocial.getSocialById(id);
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        var auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Social:Update:" + id.toString(),
                fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    /**
     * Update a Social information.
     *
     * @param proposedSocial             is the updated Social information to be saved
     * @param currentUserInformationJson is the actual user
     * @return with the id of the updated Social
     */
    public Long updateSocial(Social proposedSocial, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        var refId = proposedSocial.getId();
        var targetSocial = businessWithSocial.getSocialById(refId);
        //personId
        var newLongValue = proposedSocial.getPersonId();
        var oldLongValue = targetSocial.getPersonId();
        if (isLongChanged(oldLongValue, newLongValue)) {
            targetSocial.setPersonId(newLongValue); //if we are here, it must have been changed
            String oldValue = prepareAuditValueString(oldLongValue);
            String newValue = prepareAuditValueString(newLongValue);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "PersonId", oldValue, newValue));
        }
        //socialStatus
        var newStatus = proposedSocial.getSocialStatus();
        var oldStatus = targetSocial.getSocialStatus();
        if (!oldStatus.equals(newStatus)) {
            targetSocial.setSocialStatus(newStatus);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "SocialStatus",
                    SocialStatusTypes.getTranslatedString(oldStatus), SocialStatusTypes.getTranslatedString(newStatus)));
        }
        try {
            handleSocialUpdatePreparationFacebookPart(targetSocial, proposedSocial, auditTrailCollection, currentUserInformationJson.userName);
            handleSocialUpdatePreparationGooglePart(targetSocial, proposedSocial, auditTrailCollection, currentUserInformationJson.userName);
        } catch (DatabaseHandlingException e) {
            return null;
        }
        //comment
        var newString = proposedSocial.getComment();
        var oldString = targetSocial.getComment();
        try {
            isSocialStringFieldChangeValid(oldString, newString, currentUserInformationJson.userName);
            handleSimpleStringFieldUpdate(refId, newString, oldString, currentUserInformationJson.userName, auditTrailCollection, "Comment");
            targetSocial.setComment(newString);
        } catch (DatabaseHandlingException e) {
            return null;
        }
        return businessWithSocial.updateSocial(targetSocial, auditTrailCollection);
    }

    private void handleSocialUpdatePreparationGooglePart(Social targetSocial, Social proposedSocial, Collection<AuditTrail> auditTrailCollection, String userName) {
        var refId = proposedSocial.getId();
        String newString;
        String oldString;
        //googleUserName
        newString = proposedSocial.getGoogleUserName();
        oldString = targetSocial.getGoogleUserName();
        isSocialStringFieldChangeValid(oldString, newString, userName);
        handleSimpleStringFieldUpdate(refId, newString, oldString, userName, auditTrailCollection, "GoogleUserName");
        targetSocial.setGoogleUserName(newString);
        //googleUserPicture
        newString = proposedSocial.getGoogleUserPicture();
        oldString = targetSocial.getGoogleUserPicture();
        isSocialStringFieldChangeValid(oldString, newString, userName);
        handleSimpleStringFieldUpdate(refId, newString, oldString, userName, auditTrailCollection, "GoogleUserPicture");
        targetSocial.setGoogleUserPicture(newString);
        //googleEmail
        newString = proposedSocial.getGoogleEmail();
        oldString = targetSocial.getGoogleEmail();
        isSocialStringFieldChangeValid(oldString, newString, userName);
        handleSimpleStringFieldUpdate(refId, newString, oldString, userName, auditTrailCollection, "GoogleEmail");
        targetSocial.setGoogleEmail(newString);
        //googleUserId
        newString = proposedSocial.getGoogleUserId();
        oldString = targetSocial.getGoogleUserId();
        isSocialStringFieldChangeValid(oldString, newString, userName);
        handleSimpleStringFieldUpdate(refId, newString, oldString, userName, auditTrailCollection, "GoogleUserId");
        targetSocial.setGoogleUserId(newString);
    }

    private void handleSocialUpdatePreparationFacebookPart(Social targetSocial, Social proposedSocial, Collection<AuditTrail> auditTrailCollection, String userName) {
        var refId = proposedSocial.getId();
        String newString;
        String oldString;
        //facebookUserName
        newString = proposedSocial.getFacebookUserName();
        oldString = targetSocial.getFacebookUserName();
        isSocialStringFieldChangeValid(oldString, newString, userName);
        handleSimpleStringFieldUpdate(refId, newString, oldString, userName, auditTrailCollection, "FacebookUserName");
        targetSocial.setFacebookUserName(newString);
        //facebookFirstName
        newString = proposedSocial.getFacebookFirstName();
        oldString = targetSocial.getFacebookFirstName();
        isSocialStringFieldChangeValid(oldString, newString, userName);
        handleSimpleStringFieldUpdate(refId, newString, oldString, userName, auditTrailCollection, "FacebookFirstName");
        targetSocial.setFacebookFirstName(newString);
        //facebookEmail
        newString = proposedSocial.getFacebookEmail();
        oldString = targetSocial.getFacebookEmail();
        isSocialStringFieldChangeValid(oldString, newString, userName);
        handleSimpleStringFieldUpdate(refId, newString, oldString, userName, auditTrailCollection, "FacebookEmail");
        targetSocial.setFacebookEmail(newString);
        //facebookUserId
        newString = proposedSocial.getFacebookUserId();
        oldString = targetSocial.getFacebookUserId();
        isSocialStringFieldChangeValid(oldString, newString, userName);
        handleSimpleStringFieldUpdate(refId, newString, oldString, userName, auditTrailCollection, "FacebookUserId");
        targetSocial.setFacebookUserId(newString);
    }

    private void handleSimpleStringFieldUpdate(Long refId, String newValue, String oldValue,
                                               String userName, Collection<AuditTrail> auditTrailCollection, String fieldName) {
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(refId, userName, fieldName, oldValue, newValue));
        }
    }

    private void isSocialStringFieldChangeValid(String oldString, String newString, String userName) {
        if ((oldString == null) || (newString == null)) {
            var issue = "User: " + userName + " tried to create/update Social with null string.";
            logger.info(issue);
            throw new DatabaseHandlingException(issue);
        }
        businessWithAuditTrail.checkDangerousValue(newString, userName);
    }

    /**
     * Get the audit records of a Social record.
     *
     * @param id is the identifier of the Social record
     * @return with the list of audit events as object
     */
    public Object getSocialHistoryAsObject(Long id) {
        List<AuditTrail> auditTrailOfObject;
        auditTrailOfObject = businessWithAuditTrail.getAuditTrailOfObject(id);
        return auditTrailOfObject;
    }

    /**
     * Delete a specific Social record.
     *
     * @param deleteEntityJson identifies the Social record
     * @return with the id of the deleted Social record
     */
    public Long deleteSocial(DeleteEntityJson deleteEntityJson) {
        var id = Long.parseLong(deleteEntityJson.entityId);
        var social = businessWithSocial.getSocialById(id);
        //collect related audit records
        var auditTrailList = businessWithAuditTrail.getAuditTrailOfObject(id);
        Long result;
        result = businessWithSocial.deleteSocial(social, auditTrailList);
        return result;
    }
}
