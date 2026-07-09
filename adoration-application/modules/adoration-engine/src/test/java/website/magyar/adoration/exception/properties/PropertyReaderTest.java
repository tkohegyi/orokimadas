package website.magyar.adoration.exception.properties;


import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import website.magyar.adoration.configuration.PropertyHolder;
import website.magyar.adoration.configuration.PropertyReader;
import org.junit.Test;
import org.junit.Before;


import java.util.Properties;

import static org.junit.Assert.assertEquals;


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
        ReflectionTestUtils.setField(underTest, "propertyHolder", propertyHolder);
    }

    @Test
    public void testSetPropertiesShouldPutPropertiesToPropertyHolder() {
        //GIVEN in setUp
        properties.put("webapp.port", "1234");
        //WHEN
        underTest.setProperties(properties);
        //THEN
        PropertyHolder actual = (PropertyHolder) ReflectionTestUtils.getField(underTest, "propertyHolder");
        assertEquals(actual.getInt("webapp.port"), Integer.valueOf(1234));
    }
}
