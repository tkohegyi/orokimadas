package website.magyar.adoration.configuration;

import org.springframework.stereotype.Component;

/**
 * This class provides version title for Adoration Application.
 */
@Component
public class VersionTitleProvider {

    private static final String NOT_FOUND = "unknown (no manifest file)";

    /**
     * This method gets the actual version title from MANIFEST file.
     *
     * @return with version title
     */
    public String getVersionTitle() {
        String versionTitle = getClass().getPackage().getImplementationTitle();
        return versionTitle != null ? versionTitle : NOT_FOUND;
    }
}
