package website.magyar.adoration.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import website.magyar.adoration.web.helper.DummyTestObject;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.json.TableDataInformationJson;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.PeopleProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

/**
 * Test class of {@link AdoratorListController} class.
 */
public class AdoratorListControllerTest {

    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private PeopleProvider peopleProvider;
    @Mock
    private CurrentUserInformationJson currentUserInformationJson;

    @InjectMocks
    private AdoratorListController underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "currentUserProvider", currentUserProvider);
        Whitebox.setInternalState(underTest, "peopleProvider", peopleProvider);
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
    }

    @Test
    public void adoratorsCallByRegisteredAdoratorShallReturnWithAdoratorListJsp() {
        currentUserInformationJson.isRegisteredAdorator = true;
        //when
        String result = underTest.adorators(null);
        //then
        assertEquals("adoratorList", result);
    }

    @Test
    public void adoratorsCallByGuestShallRedirectToHome() {
        currentUserInformationJson.isRegisteredAdorator = false;
        //when
        String result = underTest.adorators(null);
        //then
        assertEquals("redirect:/adoration/", result);
    }

    @Test
    public void getPersonTableByPrivilegedAdoratorShallReturnWithPersonTable() {
        currentUserInformationJson.isRegisteredAdorator = true;
        currentUserInformationJson.isPrivilegedAdorator = true;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getAdoratorListAsObject(currentUserInformationJson, true);
        doReturn(null).when(peopleProvider).getAdoratorListAsObject(currentUserInformationJson, false);
        //when
        Object result = underTest.getPersonTable(null, null);
        //then
        assertEquals(expected, ((TableDataInformationJson) result).data);
    }

    @Test
    public void getPersonTableByNonPrivilegedAdoratorShallReturnWithPersonTable() {
        currentUserInformationJson.isRegisteredAdorator = true;
        currentUserInformationJson.isPrivilegedAdorator = false;
        DummyTestObject expected = new DummyTestObject();
        doReturn(expected).when(peopleProvider).getAdoratorListAsObject(currentUserInformationJson, false);
        doReturn(null).when(peopleProvider).getAdoratorListAsObject(currentUserInformationJson, true);
        //when
        TableDataInformationJson result = underTest.getPersonTable(null, null);
        //then
        assertEquals(expected, result.data);
    }

    @Test
    public void getPersonTableByGuestShallReturnWithNone() {
        currentUserInformationJson.isRegisteredAdorator = false;
        doReturn(new DummyTestObject()).when(peopleProvider).getAdoratorListAsObject(currentUserInformationJson, false);
        doReturn(new DummyTestObject()).when(peopleProvider).getAdoratorListAsObject(currentUserInformationJson, true);
        //when
        TableDataInformationJson result = underTest.getPersonTable(null, null);
        //then
        assertNull(result);
    }

}