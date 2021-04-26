package website.magyar.adoration.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Configures the module with the necessary properties.
 */
@Component
public class EmailConfigurationAccess implements ConfigurationAccessBase {

    private PropertyDto properties;

    @Autowired
    private PropertyHolder propertyHolder;

    /**
     * Returns a {@link PropertyDto} holding all module specific properties.
     *
     * @return the propertiesDTO object
     */
    public PropertyDto getProperties() {
        return properties;
    }

    @Override
    public void loadProperties() {
        String smtpServer = propertyHolder.get("smtp.server");
        String smtpPort = propertyHolder.get("smtp.port");
        String smtpUserName = propertyHolder.get("smtp.userName");
        String smtpPassword = propertyHolder.get("smtp.password");
        String emailFrom = propertyHolder.get("email.from");
        String emailTo = propertyHolder.get("email.to");
        properties = new PropertyDto(smtpServer, smtpPort, smtpUserName, smtpPassword, emailFrom, emailTo);
    }
}
