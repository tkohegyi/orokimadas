package website.magyar.adoration.exception.properties;


import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import website.magyar.adoration.configuration.PropertyHolder;
import website.magyar.adoration.configuration.PropertyReader;
import org.junit.Test;
import org.junit.Before;


import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;


/**
 * Unit tests for the class {@link PropertyReader}.
 */
public class PropertyReaderTest {

    private Properties properties;
    private PropertyHolder propertyHolder;

    @InjectMocks
    private PropertyReader underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        properties = new Properties();
        propertyHolder = new PropertyHolder();
        setInternalState(underTest, "propertyHolder", propertyHolder);
    }

    @Test
    public void testSetPropertiesShouldPutPropertiesToPropertyHolder() {
        //GIVEN in setUp
        properties.put("webapp.port", "1234");
        //WHEN
        underTest.setProperties(properties);
        //THEN
        PropertyHolder actual = (PropertyHolder) getInternalState(underTest, "propertyHolder");
        assertEquals(actual.getInt("webapp.port"), Integer.valueOf(1234));
    }
}
