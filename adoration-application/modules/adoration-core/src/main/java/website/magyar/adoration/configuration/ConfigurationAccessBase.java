package website.magyar.adoration.configuration;

/**
 * This interface defines a method to load all necessary properties for those classes which implement it.
 */
public interface ConfigurationAccessBase {

    /**
     * This method must create that object which stores all needed properties for the current module.
     */
    void loadProperties();
}
