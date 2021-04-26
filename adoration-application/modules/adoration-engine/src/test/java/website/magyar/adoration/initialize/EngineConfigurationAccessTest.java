package website.magyar.adoration.initialize;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import website.magyar.adoration.configuration.PropertyHolder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

/**
 * Unit tests for the class {@link EngineConfigurationAccess}.
 */
public class EngineConfigurationAccessTest {

    private PropertyDto properties;
    private Integer defaultPort = 8080;

    @Mock
    private PropertyHolder propertyHolder;

    @InjectMocks
    private EngineConfigurationAccess underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetProperties() {
        //GIVEN
        properties = new PropertyDto(defaultPort);
        Whitebox.setInternalState(underTest, "properties", properties);
        //WHEN
        PropertyDto returnedProperty = underTest.getProperties();
        //THEN
        assertEquals(returnedProperty.getPort(), defaultPort);
    }

    @Test
    public void testGetPropertyHolder() {
        //GIVEN
        Whitebox.setInternalState(underTest, "propertyHolder", propertyHolder);
        doReturn(defaultPort).when(propertyHolder).getInt("webapp.port");
        //WHEN
        underTest.loadProperties();
        //THEN
        assertEquals(underTest.getProperties().getPort(), defaultPort);
    }

}
