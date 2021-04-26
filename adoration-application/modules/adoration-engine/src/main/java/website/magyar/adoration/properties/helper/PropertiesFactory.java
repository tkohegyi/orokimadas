package website.magyar.adoration.properties.helper;

import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Factory for creating new instances of {@link Properties}.
 */
@Component
public class PropertiesFactory {

    /**
     * Creates a new instance of {@link Properties}.
     *
     * @return the new instance
     */
    public Properties createProperties() {
        return new Properties();
    }
}
