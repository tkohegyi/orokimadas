package website.magyar.adoration.engine;

import website.magyar.adoration.bootstrap.AdorationBootstrap;

/**
 * Starts the application.
 */
public final class AdorationApplication {

    public static String[] arguments; //NOSONAR

    private AdorationApplication() {
    }

    /**
     * The app main entry point.
     *
     * @param args The program needs the path of conf.properties to run.
     */
    public static void main(final String[] args) {
        arguments = args; //NOSONAR
        new AdorationBootstrap().bootstrap(args);
    }
}
