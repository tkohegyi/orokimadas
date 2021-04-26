package website.magyar.adoration.bootstrap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import website.magyar.adoration.bootstrap.helper.SystemExceptionSelector;
import website.magyar.adoration.database.SessionFactoryHelper;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.properties.PropertyLoader;
import website.magyar.adoration.web.WebAppServer;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanCreationException;

import java.util.Properties;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Unit tests for the class {@link AdorationBootstrap}.
 */
public class AdorationBootstrapTest {

    private static final String[] ARGS = {"conf.properties"};
    private static final String[] NO_ARGS = {};
    private static final String[] NOT_REAL_ARGS = {"try.this"};

    private Properties properties;
    @Mock
    private SystemExceptionSelector systemExceptionSelector;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BeanCreationException beanCreationException;
    @Mock
    private Logger logger;
    @Mock
    private WebAppServer webAppServer;
    @Mock
    private SystemException systemException;
    @Mock
    private PropertyLoader propertyLoader;
    @Mock
    private SessionFactoryHelper sessionFactoryHelper;

    @InjectMocks
    private AdorationBootstrap underTest;

    @Before
    public void setUp() {
        underTest = Mockito.spy(new AdorationBootstrap());
        MockitoAnnotations.initMocks(this);
        setInternalState(underTest, "systemExceptionSelector", systemExceptionSelector);
        setInternalState(underTest, "propertyLoader", propertyLoader);
        setInternalState(underTest, "logger", logger);
        properties = new Properties();
        given(propertyLoader.loadProperties(ARGS[0])).willReturn(properties);
        doNothing().when(sessionFactoryHelper).initiateHibernateSessionFactory(null, null, null);
    }

    @Test
    public void testBootstrapShouldCallStart() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(webAppServer).start();
    }

    @Test
    public void testBootstrapShouldLogError() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        BeanCreationException exception = new BeanCreationException("exception");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        willThrow(exception).given(webAppServer).createServer(8080, false, null, null);
        given(systemExceptionSelector.getSystemException(exception)).willReturn(systemException);
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(logger).error(Mockito.anyString());
    }

    @Test(expected = BeanCreationException.class)
    public void testBootstrapShouldThrowException() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        BeanCreationException exception = new BeanCreationException("exception");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        willThrow(exception).given(webAppServer).createServer(8080, false, null, null);
        given(systemExceptionSelector.getSystemException(exception)).willReturn(null);
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN it should throw exception
    }

    @Test
    public void testBootstrapShouldThrowExceptionWhenPropertyFileIsEmpty() {
        //GIVEN
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        //WHEN
        underTest.bootstrap(NO_ARGS);
        //THEN
        verify(logger).error(Mockito.contains("Application cannot be started: Configuration file was not specified as input argument!"));
        //verify E.TXT "Configuration file was not specified as input argument!"));
    }

    @Test
    public void testBootstrapShouldThrowExceptionWhenPropertyFileNameIsIncorrect() {
        //GIVEN
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        //WHEN
        underTest.bootstrap(NOT_REAL_ARGS);
        //THEN it should throw exception
        verify(logger).error(Mockito.contains("Application cannot be started: Configuration file must be a properties file!"));
        //verify E.TXT "Configuration file must be a properties file!"));
    }

    @Test
    public void testBootstrapWhenPortCannotBeParsedShouldThrowException() {
        //GIVEN
        properties.setProperty("webapp.port", "text");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(logger).error(Mockito.anyString());
    }
}
