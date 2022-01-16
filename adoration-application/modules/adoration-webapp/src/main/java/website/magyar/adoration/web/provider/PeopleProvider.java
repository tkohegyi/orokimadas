package website.magyar.adoration.web.provider;

import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.business.BusinessWithLink;
import website.magyar.adoration.database.business.BusinessWithNextGeneralKey;
import website.magyar.adoration.database.business.BusinessWithPerson;
import website.magyar.adoration.database.business.BusinessWithSocial;
import website.magyar.adoration.database.business.helper.DateTimeConverter;
import website.magyar.adoration.database.business.helper.enums.AdoratorStatusTypes;
import website.magyar.adoration.database.exception.DatabaseHandlingException;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Link;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.helper.EmailSender;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.DeleteEntityJson;
import website.magyar.adoration.web.json.LinkJson;
import website.magyar.adoration.web.json.MessageToCoordinatorJson;
import website.magyar.adoration.web.json.PersonInformationJson;
import website.magyar.adoration.web.json.PersonJson;
import website.magyar.adoration.web.json.RegisterAdoratorJson;
import website.magyar.adoration.web.provider.helper.ProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to provide information about users.
 */
@Component
public class PeopleProvider extends ProviderBase {

    private static final String SUBJECT_NEW_ADORATOR = "[AdoratorApp] - Új adoráló";
    private static final String SUBJECT_NEW_MESSAGE = "[AdoratorApp] - Üzenet egy felhasználótól";
    private static final int MIN_METHOD_NUMBER = 1;
    private static final int MAX_METHOD_NUMBER = 3;

    private final Logger logger = LoggerFactory.getLogger(PeopleProvider.class);

    @Autowired
    private BusinessWithPerson businessWithPerson;
    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    private BusinessWithNextGeneralKey businessWithNextGeneralKey;
    @Autowired
    private BusinessWithLink businessWithLink;
    @Autowired
    private BusinessWithSocial businessWithSocial;
    @Autowired
    private EmailSender emailSender;


    /**
     * Get simple full list of people.
     *
     * @return with the list as object
     */
    public Object getPersonListAsObject() {
        var people = businessWithPerson.getPersonList();
        return people;
    }

    /**
     * Get a specific person.
     *
     * @param id of the person
     * @return with the person as object
     */
    public Object getPersonAsObject(final Long id) {
        var person = businessWithPerson.getPersonById(id);
        return person;
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Person:Update:" + id.toString(),
                fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    /**
     * Update a Person information.
     *
     * @param personInformationJson      is the updated Person information to be saved
     * @param currentUserInformationJson is the actual user
     * @return with the id of the updated Person
     */
    public Long updatePerson(PersonInformationJson personInformationJson, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        var id = Long.parseLong(personInformationJson.id);
        var person = businessWithPerson.getPersonById(id);
        if (person == null) {
            //new Person
            id = createNewPerson(personInformationJson, currentUserInformationJson.userName);
            return id;
        }
        //prepare new name and validate it
        handleNameUpdate(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);
        //adorationStatus
        handleAdorationStatusUpdate(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);
        //other string fields
        handleAllOtherStringFields(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);
        //other boolean fields
        handleAllOtherBooleanFields(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);

        //we do not set the string languageCode
        id = businessWithPerson.updatePerson(person, auditTrailCollection);
        return id;
    }

    private void handleAllOtherBooleanFields(PersonInformationJson personInformationJson, Person person, String userName, Collection<AuditTrail> auditTrailCollection) {
        Boolean newBoolean;
        //isAnonymous
        newBoolean = handleSimpleBooleanFieldUpdate(person.getId(), personInformationJson.isAnonymous.contains("true"), person.getIsAnonymous(),
                userName, auditTrailCollection, "isAnonymous");
        person.setIsAnonymous(newBoolean);
        //mobileVisible
        newBoolean = handleSimpleBooleanFieldUpdate(person.getId(), personInformationJson.mobileVisible.contains("true"), person.getMobileVisible(),
                userName, auditTrailCollection, "MobileVisible");
        person.setMobileVisible(newBoolean);
        //emailVisible
        newBoolean = handleSimpleBooleanFieldUpdate(person.getId(), personInformationJson.emailVisible.contains("true"), person.getEmailVisible(),
                userName, auditTrailCollection, "EmailVisible");
        person.setEmailVisible(newBoolean);
        //dchSigned
        newBoolean = handleSimpleBooleanFieldUpdate(person.getId(), personInformationJson.dhcSigned.contains("true"), person.getDhcSigned(),
                userName, auditTrailCollection, "DhcSigned");
        person.setDhcSigned(newBoolean);
    }

    private void handleAllOtherStringFields(PersonInformationJson personInformationJson, Person person, String userName, Collection<AuditTrail> auditTrailCollection) {
        String newValue;
        //mobile
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.mobile.trim(), person.getMobile(),
                userName, auditTrailCollection, "Mobile");
        person.setMobile(newValue);
        //email
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.email.trim(), person.getEmail(),
                userName, auditTrailCollection, "Email");
        person.setEmail(newValue);
        //adminComment
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.adminComment.trim(), person.getAdminComment(),
                userName, auditTrailCollection, "AdminComment");
        person.setAdminComment(newValue);
        //dhcSignedDate
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.dhcSignedDate, person.getDhcSignedDate(),
                userName, auditTrailCollection, "DhcSignedDate");
        person.setDhcSignedDate(newValue);

        //finally handle the 2 string fields those can be changed by Coo-s too
        handleCooModifiableStringFields(personInformationJson, person, userName, auditTrailCollection);
    }

    private void handleCooModifiableStringFields(PersonInformationJson personInformationJson, Person person, String userName, Collection<AuditTrail> auditTrailCollection) {
        String newValue;
        //coordinatorComment
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.coordinatorComment.trim(), person.getCoordinatorComment(),
                userName, auditTrailCollection, "CoordinatorComment");
        person.setCoordinatorComment(newValue);
        //visibleComment
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.visibleComment.trim(), person.getVisibleComment(),
                userName, auditTrailCollection, "VisibleComment");
        person.setVisibleComment(newValue);
    }

    private void handleAdorationStatusUpdate(PersonInformationJson personInformationJson, Person person, String userName, Collection<AuditTrail> auditTrailCollection) {
        var newStatus = Integer.parseInt(personInformationJson.adorationStatus);
        var oldStatus = person.getAdorationStatus();
        person.setAdorationStatus(newStatus);
        if (!oldStatus.equals(newStatus)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), userName, "AdorationStatus",
                    AdoratorStatusTypes.getTranslatedString(oldStatus), AdoratorStatusTypes.getTranslatedString(newStatus)));
        }
    }

    private Boolean handleSimpleBooleanFieldUpdate(Long refId, Boolean newBoolean, Boolean oldBoolean,
                                                   String userName, Collection<AuditTrail> auditTrailCollection, String fieldName) {
        if (!oldBoolean.equals(newBoolean)) {
            auditTrailCollection.add(prepareAuditTrail(refId, userName, fieldName, oldBoolean.toString(), newBoolean.toString()));
        }
        return newBoolean;
    }

    private String handleSimpleStringFieldUpdate(Long refId, String newValue, String oldValue,
                                                 String userName, Collection<AuditTrail> auditTrailCollection, String fieldName) {
        businessWithAuditTrail.checkDangerousValue(newValue, userName);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(refId, userName, fieldName, oldValue, newValue));
        }
        return newValue;
    }

    private void handleNameUpdate(PersonInformationJson personInformationJson, Person person,
                                  String userName, Collection<AuditTrail> auditTrailCollection) {
        var newValue = personInformationJson.name.trim();
        var oldValue = person.getName();
        businessWithAuditTrail.checkDangerousValue(newValue, userName);
        //name length must be > 0, and shall not fit to other existing names
        if (newValue.length() == 0) {
            logger.info("User: {} tried to create/update Person with empty name.", userName);
            throw new DatabaseHandlingException("Field content is not allowed.");
        }
        person.setName(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), userName, "Name", oldValue, newValue));
        }
    }

    private Long createNewPerson(PersonInformationJson personInformationJson, String userName) {
        Long id;
        var person = new Person();
        person.setId(businessWithNextGeneralKey.getNextGeneralId());
        person.setName(personInformationJson.name);
        person.setAdorationStatus(Integer.parseInt(personInformationJson.adorationStatus));
        person.setAdminComment(personInformationJson.adminComment);
        person.setCoordinatorComment(personInformationJson.coordinatorComment);
        person.setDhcSigned(Boolean.getBoolean(personInformationJson.dhcSigned));
        person.setDhcSignedDate(personInformationJson.dhcSignedDate);
        person.setEmail(personInformationJson.email);
        person.setEmailVisible(Boolean.getBoolean(personInformationJson.emailVisible));
        person.setLanguageCode("hu");
        person.setMobile(personInformationJson.mobile);
        person.setMobileVisible(Boolean.getBoolean(personInformationJson.mobileVisible));
        person.setVisibleComment(personInformationJson.visibleComment);
        person.setIsAnonymous(Boolean.getBoolean(personInformationJson.isAnonymous));
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(person.getId(), userName,
                "Person:New:" + person.getId(), "Name: " + person.getName() + ", e-mail: " + person.getEmail()
                        + ", Phone: " + person.getMobile(), "");
        id = businessWithPerson.newPerson(person, auditTrail);
        return id;
    }

    /**
     * Get the audit records of a Person.
     *
     * @param id is the identifier of the Person
     * @return with the list of audit events as object
     */
    public Object getPersonHistoryAsObject(Long id) {
        return getEntityHistoryAsObject(businessWithAuditTrail, id);
    }

    /**
     * Delete a specific Person.
     *
     * @param personJson identifies the Person
     * @return with the id of the deleted Person
     */
    public Long deletePerson(DeleteEntityJson personJson) {
        var personId = Long.parseLong(personJson.entityId);
        var person = businessWithPerson.getPersonById(personId);
        //collect related social - this can be null, if there was no social for the person + we need to clear the social - person connection only
        var socialList = businessWithSocial.getSocialsOfPerson(person);
        //collect related links
        var linkList = businessWithLink.getLinksOfPerson(person);
        //collect related audit records
        var auditTrailList = businessWithAuditTrail.getAuditTrailOfObject(personId);
        Long result;
        result = businessWithPerson.deletePerson(person, socialList, linkList, auditTrailList);
        return result;
    }

    /**
     * Register a new adorator (add a new Person).
     *
     * @param adoratorJson is the person details
     * @param userName     is the name of the actual user
     * @return with the id of the registered adorator / Person
     */
    public Long registerAdorator(RegisterAdoratorJson adoratorJson, String userName) {
        //validations
        checkDangerousStringValues(adoratorJson, userName);
        if (!businessWithLink.isValidDay(adoratorJson.dayId) || !businessWithLink.isValidHour(adoratorJson.hourId)
                || !isMethodAcceptable(adoratorJson.method)) {
            logger.warn("User: {} / {} tried to use dangerous value for a new Adorator.", userName, adoratorJson.name);
            throw new DatabaseHandlingException("Field content (Integer) is not allowed.");
        }
        //if person is identified, then it is not a new adorator
        if (adoratorJson.personId != null) {
            logger.warn("User: {} / {} tried to register again.", userName, adoratorJson.name);
            throw new DatabaseHandlingException("Duplicated registration is not allowed.");
        }
        if (!adoratorJson.dhc.contentEquals("consent-yes")) { // the not null part has been checked as first thing
            logger.warn("User: {} / {} tried to register without consent.", userName, adoratorJson.name);
            throw new DatabaseHandlingException("Data handling consent is missing.");
        }
        Long id;
        id = createNewAdorator(adoratorJson);
        return id;
    }

    private void checkDangerousStringValues(RegisterAdoratorJson adoratorJson, String userName) {
        if (adoratorJson.name == null || adoratorJson.comment == null || adoratorJson.email == null
                || adoratorJson.coordinate == null || adoratorJson.dhc == null || adoratorJson.mobile == null) {
            logger.warn("User: {} tried to use null value for a new Adorator.", userName);
            throw new DatabaseHandlingException("Field content (null) is not allowed.");
        }
        businessWithAuditTrail.checkDangerousValue(adoratorJson.name, userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.comment, userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.email, userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.coordinate, userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.dhc, userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.mobile, userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.languageCode, userName);
    }

    private boolean isMethodAcceptable(Integer method) {
        return method >= MIN_METHOD_NUMBER && method <= MAX_METHOD_NUMBER;
    }

    private Long createNewAdorator(RegisterAdoratorJson adoratorJson) {
        var newId = businessWithNextGeneralKey.getNextGeneralId();
        var dateTimeConverter = new DateTimeConverter();
        var dhcSignedDate = dateTimeConverter.getCurrentDateAsString();
        adoratorJson.dhcSignedDate = dhcSignedDate;
        //send mail about the person
        var text = "New id: " + newId + "\nDHC Signed Date: " + dhcSignedDate + "\nAdatok:\n" + adoratorJson;
        emailSender.sendMailToAdministrator(SUBJECT_NEW_ADORATOR, text);
        //new Person
        var person = new Person();
        person.setId(newId);
        person.setName(adoratorJson.name);
        person.setAdorationStatus(AdoratorStatusTypes.PRE_ADORATOR.getAdoratorStatusValue());
        person.setAdminComment("Adorálás módja: " + adoratorJson.method + ", Segítség:" + adoratorJson.coordinate
                + ", DHC:" + adoratorJson.dhc + ", SelfComment: " + adoratorJson.comment);
        person.setDhcSigned(true);
        person.setDhcSignedDate(dhcSignedDate);
        person.setEmail(adoratorJson.email);
        person.setEmailVisible(true);
        person.setLanguageCode(adoratorJson.languageCode);
        person.setMobile(adoratorJson.mobile);
        person.setMobileVisible(true);
        person.setVisibleComment("");
        person.setIsAnonymous(false);
        person.setCoordinatorComment("");
        var auditTrail = businessWithAuditTrail.prepareAuditTrail(person.getId(), "SYSTEM",
                "Person:New:" + person.getId(), "Új adoráló regisztrációja.", text);
        Long id;
        id = businessWithPerson.newPerson(person, auditTrail);
        return id;
    }

    /**
     * Gets the list of the adorators (People).
     *
     * @param currentUserInformationJson is the actual user
     * @param privilegedAdorator         if the actual user is privileged or not - privileged user has right to see hidden fields.
     * @return with the list of adorators as object
     */
    public Object getAdoratorListAsObject(CurrentUserInformationJson currentUserInformationJson, Boolean privilegedAdorator) {
        var people = businessWithPerson.getPersonList();
        List<PersonJson> personList = new LinkedList<>();
        List<Link> linkList = new LinkedList<>();
        //filter out ppl and fields
        for (var p : people) {
            if (!AdoratorStatusTypes.isInactive(p.getAdorationStatus())) {
                personList.add(new PersonJson(p, privilegedAdorator));
                var personLinkList = businessWithLink.getLinksOfPerson(p);
                fillLinkListFromPersonLinkList(linkList, personLinkList);
            }
        }
        //now fill the structure
        var linkJson = new LinkJson();
        linkJson.linkList = linkList;
        linkJson.relatedPersonList = personList;
        //fill the day names
        linkJson.dayNames = currentUserInformationJson.getUserDayNames();
        return linkJson;
    }

    private void fillLinkListFromPersonLinkList(List<Link> linkList, List<Link> personLinkList) {
        if (personLinkList != null) {
            for (var l : personLinkList) {
                if (!linkList.contains(l)) {
                    l.setAdminComment(""); //empty the admin comment part, since adorators shall not see this part
                    linkList.add(l);
                }
            }
        }
    }

    /**
     * Send Email message to the main coordinator/administrator.
     *
     * @param messageToCoordinatorJson   is the message descriptor json
     * @param currentUserInformationJson is the actual user
     */
    public void messageToCoordinator(MessageToCoordinatorJson messageToCoordinatorJson, CurrentUserInformationJson currentUserInformationJson) {
        var unknown = "[ Unknown ]";
        var socialText = currentUserInformationJson.socialServiceUsed == null ? unknown : currentUserInformationJson.socialServiceUsed;
        var socialId = currentUserInformationJson.socialId == null ? unknown : currentUserInformationJson.socialId.toString();
        var personId = currentUserInformationJson.personId == null ? unknown : currentUserInformationJson.personId.toString();
        var info = messageToCoordinatorJson.info == null ? "[ Nincs adat ]" : messageToCoordinatorJson.info;
        var message = messageToCoordinatorJson.text == null ? "[ Nincs üzenet ]" : messageToCoordinatorJson.text;
        //send mail from the person
        var text = "Felhasználó neve: " + currentUserInformationJson.loggedInUserName
                + "\n  Egyéb azonosító: \n   Bejelentkezés: " + socialText + "\n   Social ID: " + socialId + "\n   Person ID: " + personId
                + "\n\n  Kapcsolat üzenet: \n" + info
                + "\n\n  Üzenet:\n" + message;
        emailSender.sendMailToAdministrator(SUBJECT_NEW_MESSAGE, text);
    }

    /**
     * Update Person by Coordinator - this means only 2 comment field can be updated.
     *
     * @param personInformationJson      is the arrived expected new person information
     * @param currentUserInformationJson is info about tha actual user
     * @return with the id of the updated record
     */
    public Long updatePersonByCoo(PersonInformationJson personInformationJson, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        var id = Long.parseLong(personInformationJson.id);
        var person = businessWithPerson.getPersonById(id);
        if (person == null) {
            //new Person? - shall not be done by Coo-s
            throw new DatabaseHandlingException("Coordinator cannot create new adorator.");
        }
        //prepare the comment fields for update
        handleCooModifiableStringFields(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);

        id = businessWithPerson.updatePerson(person, auditTrailCollection);
        return id;
    }
}
