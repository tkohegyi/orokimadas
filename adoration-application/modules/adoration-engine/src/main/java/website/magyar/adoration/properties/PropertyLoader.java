package website.magyar.adoration.properties;

import website.magyar.adoration.configuration.PropertyReader;
import website.magyar.adoration.properties.helper.FileInputStreamFactory;
import website.magyar.adoration.properties.helper.PropertiesFactory;
import website.magyar.adoration.properties.helper.PropertiesNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads properties from a configuration file.
 */
@Component
public class PropertyLoader {

    private final Logger logger = LoggerFactory.getLogger(PropertyLoader.class);

    @Value("#{programArgs}")
    private String configFile;
    @Autowired
    private FileInputStreamFactory inputStreamFactory;
    @Autowired
    private PropertiesFactory propertiesFactory;
    @Autowired
    private PropertyReader propertyReader;

    /**
     * Loads properties from the specified property file. Also validates
     * the property file.
     *
     * @param configFile the configuration file to be read
     * @return the loaded {@link Properties}
     */
    public Properties loadProperties(final String configFile) {
        Properties properties = new Properties();
        checkPropertyFileArgument(configFile);
        try (InputStream localInputStream = new FileInputStream(configFile)) {
            properties.load(localInputStream);
            logger.debug("Properties loaded from external configuration.");
        } catch (IOException e) {
            throw new PropertiesNotAvailableException("Configuration file " + configFile + " cannot be loaded", e);
        }
        return properties;
    }

    /**
     * Loads properties from the property file specified as command line argument.
     * Also validates the property file.
     */
    public void loadProperties() {
        Properties properties = propertiesFactory.createProperties();
        try {
            checkPropertyFileArgument(configFile);
            loadExternalProperties(configFile, properties);
            propertyReader.setProperties(properties);
            logger.debug("Properties loaded from external configuration.");
        } catch (IOException e) {
            throw new PropertiesNotAvailableException("Configuration file " + configFile + " cannot be loaded", e);
        }
    }

    private void checkPropertyFileArgument(final String args) {
        if (args == null || "".equals(args)) {
            throw new PropertiesNotAvailableException("Configuration file was not specified as input argument!");
        } else if (!args.endsWith(".properties")) {
            throw new PropertiesNotAvailableException("Configuration file must be a properties file!");
        }
    }

    private void loadExternalProperties(final String location, final Properties properties) throws IOException {
        try (FileInputStream inputStream = inputStreamFactory.createFileInputStream(location)) {
            properties.load(inputStream);
        }
    }

}
