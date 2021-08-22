package website.magyar.adoration.web;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import website.magyar.adoration.web.service.ServerException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for configuring, starting and stopping the jetty server.
 */

public class WebAppServer {

    private static final int STOP_TIMEOUT = 5000;
    private static final long IDLE_TIMEOUT = 50000;
    private static final String WEB_XML_LOCATION = "/WEB-INF/web.xml";
    private static final String WEBAPP_ROOT = "webapp";
    private Server server;

    /**
     * Creates and configures the webapp server.
     *
     * @param port the port the server listens on
     */
    public void createServer(final Integer port, final boolean isHttpsInUse, final String keyStoreFile, final String keyStorePassword) {
        WebAppContext context = configureWebAppContext();
        createServer(context, port, isHttpsInUse, keyStoreFile, keyStorePassword);
    }

    /**
     * Starts the jetty server.
     */
    public void start() {
        try {
            startJettyServer();
        } catch (Exception e) {
            throw new ServerException("Jetty server cannot be started. Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Stops the server.
     */
    public void stop() {
        try {
            if (server != null && server.isStarted()) {
                stopJettyServer();
            }
        } catch (Exception e) {
            throw new ServerException("Internal web application can not be stopped: " + e.getMessage(), e);
        }
    }

    private static List<ContainerInitializer> jspInitializers() {
        var sci = new JettyJasperInitializer();
        var initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<>();
        initializers.add(initializer);
        return initializers;
    }

    private WebAppContext configureWebAppContext() {
        final WebAppContext context = new WebAppContext();
        var baseUrl = getBaseUrl();
        context.setDescriptor(baseUrl + WEB_XML_LOCATION);
        context.setResourceBase(baseUrl + "");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/.*jsp-api-[^/]*\\.jar$|.*/.*jsp-[^/]*\\.jar$|.*/.*taglibs[^/]*\\.jar$");

        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        context.addBean(new ServletContainerInitializersStarter(context), true);

        var errorHandler = new ErrorPageErrorHandler();
        errorHandler.addErrorPage(HttpStatus.NOT_FOUND.value(), "/adoration/e404");
        errorHandler.addErrorPage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "/adoration/e500");
        context.setErrorHandler(errorHandler);
        return context;
    }

    void createServer(final WebAppContext context, final Integer port, final boolean isHttpsInUse,
                      final String keyStoreFile, final String keyStorePassword) {
        server = new Server(port);
        server.setStopAtShutdown(true);
        server.setStopTimeout(STOP_TIMEOUT);
        server.setHandler(context);

        //HTTPS part
        if (isHttpsInUse) {
            HttpConfiguration httpConfiguration = new HttpConfiguration();
            httpConfiguration.setSecureScheme("https");
            httpConfiguration.setSecurePort(port);
            HttpConfiguration httpsConfig = new HttpConfiguration(httpConfiguration);
            httpsConfig.addCustomizer(new SecureRequestCustomizer());
            SslContextFactory sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(keyStoreFile);
            sslContextFactory.setKeyStorePassword(keyStorePassword);
            ServerConnector httpsConnector = new ServerConnector(server,  //NOSONAR - java:S2095 / Resources should be closed - well, this one should not
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(httpsConfig));
            httpsConnector.setPort(port);
            httpsConnector.setIdleTimeout(IDLE_TIMEOUT);
            server.setConnectors(new Connector[]{httpsConnector});
        }
    }

    void startJettyServer() throws Exception {
        server.start();
        server.join();
    }

    void stopJettyServer() throws Exception {
        server.stop();
        server.join();
    }

    private String getBaseUrl() {
        var webInfUrl = WebAppServer.class.getClassLoader().getResource(WEBAPP_ROOT);
        return webInfUrl.toExternalForm();
    }

}
