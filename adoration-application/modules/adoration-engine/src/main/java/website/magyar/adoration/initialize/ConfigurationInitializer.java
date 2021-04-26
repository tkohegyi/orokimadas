package website.magyar.adoration.initialize;

import website.magyar.adoration.bootstrap.StartUpMessageGenerator;
import website.magyar.adoration.configuration.ConfigurationAccessBase;
import website.magyar.adoration.exception.SystemException;
import website.magyar.adoration.properties.PropertyLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Loads and initialises all the configured properties, to make it available inside the application.
 */
@Component
public class ConfigurationInitializer {

    @Autowired
    private PropertyLoader propertyLoader;
    @Autowired
    private StartUpMessageGenerator startUpMessageGenerator;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private List<ConfigurationAccessBase> configurationAccesses;


    /**
     * This method reads in property file of adorationApplication.
     *
     * @throws SystemException {@link PropertyLoader}, {@link ApplicationContext} can throw different exceptions.
     */
    @PostConstruct
    void afterPropertiesSet() {
        propertyLoader.loadProperties();
        loadProperties();
        startUpMessageGenerator.logStartUpMessage();
    }

    private void loadProperties() {
        for (ConfigurationAccessBase configAccess : configurationAccesses) {
            configAccess.loadProperties();
        }
    }

}
