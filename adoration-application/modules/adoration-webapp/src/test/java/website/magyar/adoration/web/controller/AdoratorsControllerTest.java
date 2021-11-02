package website.magyar.adoration.web.controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import website.magyar.adoration.database.exception.DatabaseHandlingException;
import website.magyar.adoration.web.helper.DummyTestObject;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.PersonInformationJson;
import website.magyar.adoration.web.json.TableDataInformationJson;
import website.magyar.adoration.web.provider.CoverageProvider;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.PeopleProvider;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Test class of {@link AdoratorsController} class.
 */
public class AdoratorsControllerTest {

    @Mock
    private Logger logger;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private PeopleProvider peopleProvider;
    @Mock
    private CoverageProvider coverageProvider;
    @Mock
    private CurrentUserInformationJson currentUserInformationJson;

    @InjectMocks
    private AdoratorsController underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "currentUserProvider", currentUserProvider);
        Whitebox.setInternalState(underTest, "peopleProvider", peopleProvider);
        Whitebox.setInternalState(underTest, "coverageProvider", coverageProvider);
        Whitebox.setInternalState(underTest, "logger", logger);
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
    }

    @Test
    public void adoratorsForAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = true;
        //when
        String result = underTest.adorators(null);
        //then
        assertEquals("adorators", result);
    }

    @Test
    public void adoratorsForNonAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = false;
        //when
        String result = underTest.adorators(null);
        //then
        assertEquals("redirect:/adoration/", result);
    }

    @Test
    public void getPersonTableForAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = true;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getPersonListAsObject();
        //when
        TableDataInformationJson result = underTest.getPersonTable(null, null);
        //then
        assertEquals(expected, result.data);
    }

    @Test
    public void getPersonTableForNonAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = false;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getPersonListAsObject();
        //when
        TableDataInformationJson result = underTest.getPersonTable(null, null);
        //then
        assertNull(result);
    }

    @Test
    public void getPersonByIdForAdministrator() {
        currentUserInformationJson.isPrivilegedAdorator = true;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getPersonAsObject(1L);
        //when
        TableDataInformationJson result = underTest.getPersonById(null, "1");
        //then
        assertEquals(expected, result.data);
    }

    @Test
    public void getPersonByIdForAdministratorButIncorrectId() {
        currentUserInformationJson.isPrivilegedAdorator = true;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getPersonAsObject(1L);
        //when
        TableDataInformationJson result = underTest.getPersonById(null, "not a number");
        //then
        assertNull(result);
        verify(logger).warn("Rouge request to getPerson endpoint with bad id.");
    }

    @Test
    public void getPersonByIdForNonAdministrator() {
        currentUserInformationJson.isPrivilegedAdorator = false;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getPersonAsObject(1L);
        //when
        TableDataInformationJson result = underTest.getPersonById(null, "1");
        //then
        assertNull(result);
    }

    @Test
    public void getPersonHistoryByIdForAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = true;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getPersonHistoryAsObject(1L);
        //when
        TableDataInformationJson result = underTest.getPersonHistoryById(null, "1");
        //then
        assertEquals(expected, result.data);
    }

    @Test
    public void getPersonHistoryByIdForAdministratorButIncorrectId() {
        currentUserInformationJson.isAdoratorAdmin = true;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getPersonHistoryAsObject(1L);
        //when
        TableDataInformationJson result = underTest.getPersonHistoryById(null, "not a number");
        //then
        assertNull(result);
        verify(logger).warn("Rouge request to getPersonHistory endpoint with bad id.");
    }

    @Test
    public void getPersonHistoryByIdForNonAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = false;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getPersonHistoryAsObject(1L);
        //when
        TableDataInformationJson result = underTest.getPersonHistoryById(null, "1");
        //then
        assertNull(result);
    }

    @Ignore
    @Test
    public void getPersonCommitmentByIdForAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = true;
        currentUserInformationJson.languageCode = "hu";
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(coverageProvider).getPersonCommitmentAsObject(1L, new CurrentUserInformationJson());
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
        //when
        TableDataInformationJson result = underTest.getPersonCommitmentsById(null, "1");
        //then
        assertEquals(expected, result.data);
    }

    @Test
    public void getPersonCommitmentByIdForAdministratorButIncorrectId() {
        currentUserInformationJson.isAdoratorAdmin = true;
        currentUserInformationJson.languageCode = "hu";
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(coverageProvider).getPersonCommitmentAsObject(1L, new CurrentUserInformationJson());
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
        //when
        TableDataInformationJson result = underTest.getPersonCommitmentsById(null, "not a number");
        //then
        assertNull(result);
        verify(logger).warn("Rouge request to getPersonCommitments endpoint with bad id.");
    }

    @Test
    public void getPersonCommitmentByIdForNonAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = false;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(coverageProvider).getPersonCommitmentAsObject(1L, new CurrentUserInformationJson());
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
        //when
        TableDataInformationJson result = underTest.getPersonCommitmentsById(null, "1");
        //then
        assertNull(result);
    }

    @Test
    public void updatePersonIsNotAvailableForNonAdministrator() {
        currentUserInformationJson.isAdoratorAdmin = false;
        //when
        ResponseEntity<String> result = underTest.updatePerson(null, null);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("{\"entityUpdate\":\"Unauthorized action.\"}", result.getBody());
    }

    @Test
    public void updatePersonByAdministratorSuccess() {
        currentUserInformationJson.isAdoratorAdmin = true;
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
        String personJson = "{\"id\":\"1\"}";
        doReturn(1L).when(peopleProvider).updatePerson(any(PersonInformationJson.class), eq(currentUserInformationJson));
        //when
        ResponseEntity<String> result = underTest.updatePerson(personJson, null);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("{\"entityUpdate\":\"OK-1\"}", result.getBody());
    }

    @Test
    public void updatePersonByAdministratorFailureNormal() {
        currentUserInformationJson.isAdoratorAdmin = true;
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
        String personJson = "{\"id\":\"1\"}";
        doReturn(null).when(peopleProvider).updatePerson(any(PersonInformationJson.class), eq(currentUserInformationJson));
        //when
        ResponseEntity<String> result = underTest.updatePerson(personJson, null);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("{\"entityUpdate\":\"Cannot update the Person, please check the values and retry.\"}", result.getBody());
        verify(logger).info("Cannot update the Person with ID: {}", "1");
    }

    @Test
    public void updatePersonByAdministratorFailureBadRequestContent() {
        currentUserInformationJson.isAdoratorAdmin = true;
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
        String personJson = "blah";
        doReturn(1L).when(peopleProvider).updatePerson(any(PersonInformationJson.class), eq(currentUserInformationJson));
        //when
        ResponseEntity<String> result = underTest.updatePerson(personJson, null);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("{\"entityUpdate\":\"Cannot update the Person, please contact to maintainers.\"}", result.getBody());
        verify(logger).warn(eq("Error happened at update Person, pls contact to maintainers"), any(Exception.class));
    }

    @Test
    public void updatePersonByAdministratorFailureBadDatabaseUpdate() {
        currentUserInformationJson.isAdoratorAdmin = true;
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
        String personJson = "{\"id\":\"1\"}";
        String expected = "Issue";
        doThrow(new DatabaseHandlingException(expected)).when(peopleProvider).updatePerson(any(PersonInformationJson.class), eq(currentUserInformationJson));
        //when
        ResponseEntity<String> result = underTest.updatePerson(personJson, null);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("{\"entityUpdate\":\"" + expected + "\"}", result.getBody());
    }

}