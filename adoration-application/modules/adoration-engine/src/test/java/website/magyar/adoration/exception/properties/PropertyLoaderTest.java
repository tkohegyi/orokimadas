package website.magyar.adoration.exception.properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import website.magyar.adoration.configuration.PropertyReader;
import website.magyar.adoration.properties.PropertyLoader;
import website.magyar.adoration.properties.helper.FileInputStreamFactory;
import website.magyar.adoration.properties.helper.PropertiesFactory;
import website.magyar.adoration.properties.helper.PropertiesNotAvailableException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.reflect.internal.WhiteboxImpl.setInternalState;

/**
 * Unit tests for the clasd {@link PropertyLoader}.
 */
public class PropertyLoaderTest {

    private final String configFile = "src/test/resources/conf.properties";
    @Mock
    private Properties properties;
    @Mock
    private FileInputStream inputStream;
    @Mock
    private FileInputStreamFactory inputStreamFactory;
    @Mock
    private PropertiesFactory propertiesFactory;
    @Mock
    private PropertyReader propertyReader;

    @InjectMocks
    private PropertyLoader underTest;

    @Before
    public void setUp() throws IOException {
        underTest = Mockito.spy(new PropertyLoader());
        MockitoAnnotations.initMocks(this);
        given(inputStreamFactory.createFileInputStream(configFile)).willReturn(inputStream);
        given(propertiesFactory.createProperties()).willReturn(properties);
        setInternalState(underTest, "configFile", configFile);
    }

    @Test(expected = PropertiesNotAvailableException.class)
    public void testLoadPropertiesWhenProgramArgumentEmptyShouldThrowException() {
        //GIVEN
        setInternalState(underTest, "configFile", "");
        //WHEN
        underTest.loadProperties();
        //THEN excpetion should be thrown
    }

    @Test(expected = PropertiesNotAvailableException.class)
    public void testLoadPropertiesWhenProgramArgumentInvalidShouldThrowException() {
        //GIVEN
        setInternalState(underTest, "configFile", "adorator.conf.prop");
        //WHEN
        underTest.loadProperties();
        //THEN exception should be thrown
    }

    @Test
    public void testLoadPropertiesWhenArgumentsAreValidShouldLoadProperties() throws IOException {
        //GIVEN in setUp
        //WHEN
        underTest.loadProperties();
        //THEN
        verify(properties).load(inputStream);
    }

    @Test
    public void testLoadPropertiesWhenArgumentsAreValidShouldCallPropertyReader() {
        //GIVEN in setUp
        //WHEN
        underTest.loadProperties();
        //THEN
        verify(propertyReader).setProperties(properties);
    }

    @Test(expected = PropertiesNotAvailableException.class)
    public void testLoadPropertiesWhenFileNotFoundShouldThrowException() throws Exception {
        //GIVEN
        given(inputStreamFactory.createFileInputStream(configFile)).willThrow(new FileNotFoundException());
        //WHEN
        underTest.loadProperties();
        //THEN exception should be thrown

    }

    @Test(expected = PropertiesNotAvailableException.class)
    public void testLoadPropertiesWhenIOExcpetionShouldThrowException() throws Exception {
        //GIVEN
        willThrow(new IOException()).given(properties).load(inputStream);
        //WHEN
        underTest.loadProperties();
        //THEN exception should be thrown
    }

    @Test
    public void testLoadPropertiesShouldReturnProperties() {
        //GIVEN in setUp
        //WHEN
        Properties actual = underTest.loadProperties(configFile);
        //THEN
        assertEquals("8080", actual.getProperty("webapp.port"));
    }

    @Test(expected = PropertiesNotAvailableException.class)
    public void testLoadPropertiesShouldThrowExceptionWhenPropertiesNotFound() {
        //GIVEN in setUp
        //WHEN
        underTest.loadProperties("conf.properties");
        //THEN exception should be thrown
    }

}
