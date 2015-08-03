package com.googlecode.utterlyidle.jetty.eclipse;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ApplicationBuilder;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import com.googlecode.utterlyidle.servlet.ServletModule;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.ServletContext;
import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.functions.Time0.calculateMilliseconds;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static java.lang.String.format;
import static java.lang.System.nanoTime;

public class RestServer implements com.googlecode.utterlyidle.Server {
    private final Application application;
    private final ServerConfiguration configuration;
    private Server server;
    private Uri uri;
    private final Function1<? super Server, ? extends ServletContextHandler> contextCreator;
    private ServletContextHandler context;

    private RestServer(final Application application, final ServerConfiguration configuration, Function1<? super Server, ? extends ServletContextHandler> contextCreator) throws Exception {
        this.application = application;
        this.configuration = configuration;
        this.contextCreator = contextCreator;
        server = startApp();
    }

    public static RestServer restServer(final Application application, final ServerConfiguration configuration, Function1<? super Server, ? extends ServletContextHandler> contextCreator) throws Exception {
        return new RestServer(application, configuration, contextCreator);
    }

    public static RestServer restServer(final Application application, final ServerConfiguration configuration) throws Exception {
        return restServer(application, configuration, defaultContext(application, configuration));
    }

    public void close() throws IOException {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationBuilder.application(HelloWorldApplication.class).start(defaultConfiguration().port(8002));
    }

    private Server startApp() throws Exception {
        long start = nanoTime();
        Server server = startUpServer();
        System.out.println(format("Listening on %s, started Jetty in %s msecs", uri, calculateMilliseconds(start, nanoTime())));
        Service.functions.start().callConcurrently(application);
        return server;
    }

    private Server startUpServer() throws Exception {
        Server server = createServer(configuration);

        context = contextCreator.call(server);
        server.start();

        servletContext().setAttribute(Application.class.getCanonicalName(), application);
        uri = configuration.port(portNumber(server)).toUrl();
        return server;
    }

    public static Function1<Server, ServletContextHandler> defaultContext(final Application application, final ServerConfiguration configuration) {
        return new Function1<Server, ServletContextHandler>() {
            @Override
            public ServletContextHandler call(Server server) throws Exception {
                ServletContextHandler context = new ServletContextHandler(server, contextPath(configuration.basePath()), false, false);
                application.add(new ServletModule(context.getServletContext()));
                context.addServlet(ApplicationServlet.class, "/*");
                return context;
            }
        };
    }

    static String contextPath(BasePath basePath) {
        return removeTrailingSlash(basePath.toString());
    }

    private static String removeTrailingSlash(final String value) {
        if (!value.endsWith("/")) return value;
        return value.substring(0, value.length() - 1);
    }

    private Server createServer(ServerConfiguration serverConfig) {
        Server server = new Server(new QueuedThreadPool(serverConfig.maxThreadNumber()));
        ServerConnector serverConnector = new ServerConnector(server, new HttpConnectionFactory());
        serverConnector.setPort(serverConfig.port());
        serverConnector.setHost(serverConfig.bindAddress().getHostAddress());
        server.addConnector(serverConnector);
        return server;
    }

    private int portNumber(Server server) {
        return sequence(server.getConnectors()).safeCast(ServerConnector.class).head().getLocalPort();
    }

    @Override
    public Application application() {
        return application;
    }

    public Uri uri() {
        return uri;
    }

    public ServletContext servletContext() {
        return context.getServletContext();
    }

    static {
        org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
    }
}
