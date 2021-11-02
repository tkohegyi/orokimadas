package website.magyar.adoration.web.provider;

import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.database.business.BusinessWithCoordinator;
import website.magyar.adoration.database.business.BusinessWithNextGeneralKey;
import website.magyar.adoration.database.business.BusinessWithPerson;
import website.magyar.adoration.database.business.helper.enums.CoordinatorTypes;
import website.magyar.adoration.database.tables.AuditTrail;
import website.magyar.adoration.database.tables.Coordinator;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.web.json.CoordinatorJson;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.DeleteEntityJson;
import website.magyar.adoration.web.provider.helper.ProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to provide Coordinator related information and update possibilities.
 */
@Component
public class CoordinatorProvider extends ProviderBase {

    private final Logger logger = LoggerFactory.getLogger(CoordinatorProvider.class);

    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    private BusinessWithCoordinator businessWithCoordinator;
    @Autowired
    private BusinessWithPerson businessWithPerson;
    @Autowired
    private BusinessWithNextGeneralKey businessWithNextGeneralKey;

    private CoordinatorJson getCoordinatorJsonFromCoordinator(CurrentUserInformationJson currentUserInformationJson, Coordinator coordinator) {
        var coordinatorJson = new CoordinatorJson();
        coordinatorJson.id = coordinator.getId().toString();
        coordinatorJson.coordinatorType = coordinator.getCoordinatorType().toString();
        coordinatorJson.coordinatorTypeText = currentUserInformationJson.getLanguageString("coordinator." + coordinatorJson.coordinatorType);
        if (coordinator.getPersonId() != null) {
            coordinatorJson.personId = coordinator.getPersonId().toString();
            Person p = businessWithPerson.getPersonById(coordinator.getPersonId());
            coordinatorJson.personName = p.getName();
            coordinatorJson.phone = p.getMobile();
            coordinatorJson.eMail = p.getEmail();
            coordinatorJson.visibleComment = p.getVisibleComment();
        } else {
            coordinatorJson.personId = "";
            coordinatorJson.personName = "";
            coordinatorJson.phone = "";
            coordinatorJson.eMail = "";
            coordinatorJson.visibleComment = "";
        }
        return coordinatorJson;
    }

    /**
     * Gets full list of json coordinators information as object.
     *
     * @param currentUserInformationJson is info about the actual user
     * @return with the object
     */
    public Object getCoordinatorListAsObject(CurrentUserInformationJson currentUserInformationJson) {
        var coordinatorList = getCoordinatorList();
        var coordinatorJsonList = new LinkedList<>();
        for (var coordinator : coordinatorList) {
            CoordinatorJson coordinatorJson = getCoordinatorJsonFromCoordinator(currentUserInformationJson, coordinator);
            coordinatorJsonList.add(coordinatorJson);
        }
        return coordinatorJsonList;
    }

    /**
     * Gets full list of coordinators as Coordinator list.
     *
     * @return with the list of Coordinators
     */
    public List<Coordinator> getCoordinatorList() {
        List<Coordinator> coordinatorList;
        coordinatorList = businessWithCoordinator.getList();
        return coordinatorList;
    }

    /**
     * Gets info on a specific coordinator as object.
     *
     * @param id                         identified the coordinator
     * @param currentUserInformationJson info about tha actual user
     * @return with the json coordinator object, filled according to the rights of the user
     */
    public Object getCoordinatorAsObject(final Long id, CurrentUserInformationJson currentUserInformationJson) {
        var coordinator = businessWithCoordinator.getById(id);
        CoordinatorJson coordinatorJson;
        coordinatorJson = getCoordinatorJsonFromCoordinator(currentUserInformationJson, coordinator);
        return coordinatorJson;
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Coordinator:Update:" + id.toString(),
                fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    private Long createCoordinator(Coordinator newCoordinator, CurrentUserInformationJson currentUserInformationJson) {
        Long id;
        var coordinator = new Coordinator();
        coordinator.setId(businessWithNextGeneralKey.getNextGeneralId());
        coordinator.setCoordinatorType(newCoordinator.getCoordinatorType());
        var personId = newCoordinator.getPersonId();
        var personString = "N/A";
        if (personId != null) {
            var person = businessWithPerson.getPersonById(personId);
            if (person == null) {
                logger.info("User: {} tried to create Coordinator with non-existing person.", currentUserInformationJson.userName);
                return null;
            }
            personString = "ID: " + person.getId().toString() + ", Name: " + person.getName();
        }
        coordinator.setPersonId(personId);
        var auditTrail = businessWithAuditTrail.prepareAuditTrail(coordinator.getId(), currentUserInformationJson.userName,
                "Coordinator:New:" + coordinator.getId(),
                "CoordinatorType: " + CoordinatorTypes.getTypeFromId(coordinator.getCoordinatorType()).toString() + ", Person: " + personString, "");
        id = businessWithCoordinator.newCoordinator(coordinator, auditTrail);
        return id;
    }

    /**
     * Update an existing Coordinator.
     *
     * @param coordinatorJson            is the coordinator json to be used for the update event
     * @param currentUserInformationJson info about the actual user
     * @return with the id of the updated coordinator
     */
    public Long updateCoordinator(CoordinatorJson coordinatorJson, CurrentUserInformationJson currentUserInformationJson) {
        var newCoordinator = new Coordinator();
        newCoordinator.setId(Long.parseLong(coordinatorJson.id));
        newCoordinator.setCoordinatorType(CoordinatorTypes.getTypeFromId(Integer.parseInt(coordinatorJson.coordinatorType)).getCoordinatorValue());
        if (coordinatorJson.personId.length() == 0) {
            newCoordinator.setPersonId(null);
        } else {
            newCoordinator.setPersonId(Long.parseLong(coordinatorJson.personId));
        }
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        var id = newCoordinator.getId();
        var coordinator = businessWithCoordinator.getById(id);
        if (coordinator == null) { //need to create a new one
            return createCoordinator(newCoordinator, currentUserInformationJson);
        }
        var refId = coordinator.getId();
        //coordinatorType
        var newIntValue = newCoordinator.getCoordinatorType();
        var oldIntValue = coordinator.getCoordinatorType();
        if (!newIntValue.equals(oldIntValue)) {
            CoordinatorTypes.getTypeFromId(newIntValue); //validation of value
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "CoordinatorType",
                    CoordinatorTypes.getTypeFromId(oldIntValue).toString(),
                    CoordinatorTypes.getTypeFromId(newIntValue).toString()));
        }
        //personId
        var newLongValue = newCoordinator.getPersonId();
        var oldLongValue = coordinator.getPersonId();
        if (newLongValue != null) {
            var person = businessWithPerson.getPersonById(newLongValue);
            if (person == null) {
                logger.info("User: {} tried to update Coordinator with non-existing person.", currentUserInformationJson.userName);
                return null;
            }
        }
        if (isLongChanged(oldLongValue, newLongValue)) {
            String oldValue = prepareAuditValueString(oldLongValue);
            String newValue = prepareAuditValueString(newLongValue);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "PersonId", oldValue, newValue));
        }
        id = businessWithCoordinator.updateCoordinator(newCoordinator, auditTrailCollection);
        return id;
    }

    /**
     * Gets the list of audit events associated to a specific object Id.
     *
     * @param id identifies the record we are interested about its history
     * @return with the list of audit trail that belong to the specified item, as object
     */
    public Object getCoordinatorHistoryAsObject(Long id) {
        return getEntityHistoryAsObject(businessWithAuditTrail, id);
    }

    /**
     * Delete an existing coordinator.
     *
     * @param p                          is the entity info to be deleted
     * @param currentUserInformationJson is the actual user
     * @return with the id of the deleted entity
     */
    public Long deleteCoordinator(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        var id = Long.parseLong(p.entityId);
        var coordinator = businessWithCoordinator.getById(id);
        //collect related audit records
        var auditTrailList = businessWithAuditTrail.getAuditTrailOfObject(id);
        var result = businessWithCoordinator.deleteCoordinator(coordinator, auditTrailList);
        return result;
    }

    /**
     * Gets the leader coordinators as list of json records.
     *
     * @param currentUserInformationJson is the actual user
     * @return with the list of json records
     */
    public List<CoordinatorJson> getLeadership(CurrentUserInformationJson currentUserInformationJson) {
        var coordinatorList = businessWithCoordinator.getList();
        //sort here by type
        coordinatorList.sort(Comparator.comparing(Coordinator::getCoordinatorType));
        List<CoordinatorJson> coordinatorJsonList = new LinkedList<>();
        for (var coordinator : coordinatorList) {
            var coordinatorJson = getCoordinatorJsonFromCoordinator(currentUserInformationJson, coordinator);
            coordinatorJsonList.add(coordinatorJson);
        }
        return coordinatorJsonList;
    }
}
