package website.magyar.adoration.web.controller;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import website.magyar.adoration.web.helper.DummyTestObject;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.LogFileProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

/**
 * Unit test for {@link AppLogController} class.
 */
public class AppLogControllerTest {

    private static final String JSON_NAME = "files";
    private static final String NOT_IMPORTANT = "";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_TEMPLATE = "attachment; filename=%s";

    @Mock
    private RequestMappingHandlerMapping handlerMapping;
    @Mock
    private LogFileProvider logFileProvider;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private CurrentUserInformationJson currentUserInformationJson;

    @InjectMocks
    private AppLogController underTest;

    @Before
    public void setUp() throws NoSuchMethodException {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "currentUserProvider", currentUserProvider);
        Whitebox.setInternalState(underTest, "logFileProvider", logFileProvider);
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);

        //initiate registered methods
        Map<RequestMappingInfo, HandlerMethod> methods = new HashMap<>();
        methods.put(RequestMappingInfo.paths("*").build(), new HandlerMethod(new DummyTestObject(), DummyTestObject.class.getMethod("getTrue")));
        doReturn(methods).when(handlerMapping).getHandlerMethods();
        Whitebox.setInternalState(underTest, "handlerMapping", handlerMapping);
    }

    @Test
    public void appLogCallByRegisteredAdoratorShallReturnWithApplogJsp() {
        currentUserInformationJson.isAdoratorAdmin = true;
        //when
        String result = underTest.appLog(null, null);
        //then
        assertEquals("applog", result);
    }

    @Test
    public void appLogCallByGuestShallRedirectToHome() {
        currentUserInformationJson.isAdoratorAdmin = false;
        //when
        String result = underTest.appLog(null, null);
        //then
        assertEquals("redirect:/adoration/", result);
    }

    @Test
    public void testGetLogFilesShouldRespondWithLogFilesWithJsonName() {
        //GIVEN
        Map<String, Collection<String>> expected = new HashMap<>();
        Collection<String> fileNames = new ArrayList<>();
        fileNames.add("a");
        expected.put(JSON_NAME, fileNames);
        given(logFileProvider.getLogFileNames()).willReturn(fileNames);
        Whitebox.setInternalState(currentUserInformationJson, "isAdoratorAdmin", true);
        //WHEN
        Map<String, Collection<String>> result = underTest.getLogFiles(null);
        //THEN
        assertEquals(expected, result);
    }

    @Test
    public void testGetLogFilesShouldNotRespondWhenUserIsNotAdministrator() {
        //GIVEN
        Map<String, Collection<String>> expected = new HashMap<>();
        Collection<String> fileNames = new ArrayList<>();
        fileNames.add("a");
        expected.put(JSON_NAME, fileNames);
        given(logFileProvider.getLogFileNames()).willReturn(fileNames);
        Whitebox.setInternalState(currentUserInformationJson, "isAdoratorAdmin", false);
        //WHEN
        Map<String, Collection<String>> result = underTest.getLogFiles(null);
        //THEN
        assertEquals(0, result.size());
    }

    @Test
    public void testGetLogFileContentWhenSourceIsTrueShouldNotSetContentDisposition() {
        //GIVEN
        String expectedBody = "content";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(expectedBody);
        Whitebox.setInternalState(currentUserInformationJson, "isAdoratorAdmin", true);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(null, fileName, true, NOT_IMPORTANT);
        //THEN
        assertEquals(MediaType.TEXT_PLAIN, result.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getHeaders().get(CONTENT_DISPOSITION));
        assertEquals(expectedBody, result.getBody());
    }

    @Test
    public void testGetLogFileContentWhenSourceIsFalseShouldSetContentDispositionToAttachment() {
        //GIVEN
        String expectedBody = "content";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(expectedBody);
        Whitebox.setInternalState(currentUserInformationJson, "isAdoratorAdmin", true);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(null, fileName, false, NOT_IMPORTANT);
        //THEN
        assertEquals(MediaType.TEXT_PLAIN, result.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(String.format(ATTACHMENT_TEMPLATE, fileName), result.getHeaders().getFirst(CONTENT_DISPOSITION));
        assertEquals(expectedBody, result.getBody());
    }

    @Test
    public void testGetLogFileContentWhenUserIsOnWindowsShouldConvertLineBreaks() {
        //GIVEN
        String expectedBody = "content\r\n";
        String userAgentWindows = "SOMETHINGSOMETHING-WINDOWS";
        String body = "content\n";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(body);
        Whitebox.setInternalState(currentUserInformationJson, "isAdoratorAdmin", true);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(null, fileName, true, userAgentWindows);
        //THEN
        assertEquals(expectedBody, result.getBody());
    }

    @Test
    public void testGetLogFileContentWhenUserIsNotAdministratorShouldReturnErrorString() {
        //GIVEN
        String expectedBody = "Unauthorized action."; //this is the expected error message
        String userAgentWindows = "SOMETHINGSOMETHING-WINDOWS";
        String body = "content\n";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(body);
        Whitebox.setInternalState(currentUserInformationJson, "isAdoratorAdmin", false);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(null, fileName, true, userAgentWindows);
        //THEN
        assertEquals(expectedBody, result.getBody());
    }

    @Test
    public void testShowEndPointsForAdministrator() {
        Whitebox.setInternalState(currentUserInformationJson, "isAdoratorAdmin", true);
        //when
        Map<String, Collection<String>> result = underTest.showEndPoints(null);
        //then
        assertEquals(1, result.size());
    }

    @Test
    public void testShowEndPointsForNotAdministrator() {
        Whitebox.setInternalState(currentUserInformationJson, "isAdoratorAdmin", false);
        //when
        Map<String, Collection<String>> result = underTest.showEndPoints(null);
        //then
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAdorationAppServerInfo() {
        //given
        //when
        Map<String, Collection<String>> result = underTest.getAdorationAppServerInfo();
        //then
        assertEquals(1, result.size());
        assertNotNull(result.get("adorAppApplication").toArray()[0]);
        String data = result.get("adorAppApplication").toArray()[0].toString();
        assertTrue(data.contains("ip"));
        assertTrue(data.contains("hostname"));
        JSONObject jsonObj = new JSONObject(data);
        assertFalse(jsonObj.get("ip").toString().isEmpty());
        assertFalse(jsonObj.get("hostname").toString().isEmpty());
    }

}
