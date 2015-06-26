package com.googlecode.utterlyidle.jetty;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ApplicationBuilder;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import com.googlecode.utterlyidle.servlet.ServletModule;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

import javax.servlet.ServletContext;
import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.functions.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static org.mortbay.jetty.servlet.Context.NO_SESSIONS;

public class RestServer implements com.googlecode.utterlyidle.Server {
    private final Application application;
    private final ServerConfiguration configuration;
    private Server server;
    private Uri uri;
    private final Function1<? super Server, ? extends Context> contextCreator;
    private Context context;

    private RestServer(final Application application, final ServerConfiguration configuration, Function1<? super Server, ? extends Context> contextCreator) throws Exception {
        this.application = application;
        this.configuration = configuration;
        this.contextCreator = contextCreator;
        server = startApp();
    }

    public static RestServer restServer(final Application application, final ServerConfiguration configuration, Function1<? super Server, ? extends Context> contextCreator) throws Exception {
        return new RestServer(application, configuration, contextCreator);
    }

    public static RestServer restServer(final Application application, final ServerConfiguration configuration) throws Exception {
        return restServer(application, configuration, defaultContext(application, configuration));
    }

    public static RestServer webXmlRestServer(final Application application, final ServerConfiguration configuration, Uri webRoot) throws Exception {
        return restServer(application, configuration, webXmlContext(webRoot, configuration));
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

    public static Function1<Server, Context> defaultContext(final Application application, final ServerConfiguration configuration) {
        return server1 -> {
            Context context1 = new Context(server1, contextPath(configuration.basePath()), NO_SESSIONS);
            application.add(new ServletModule(context1.getServletContext()));
            context1.addServlet(ApplicationServlet.class, "/*");
            return context1;
        };
    }

    private static String contextPath(BasePath basePath) {
        return removeTrailingSlash(basePath.toString());
    }

    private static String removeTrailingSlash(final String value) {
        if (!value.endsWith("/")) return value;
        return value.substring(0, value.length() - 1);
    }

    public static Function1<Server, Context> webXmlContext(final Uri webRoot, final ServerConfiguration configuration) {
        return server1 -> new WebAppContext(server1, webRoot.toString(), contextPath(configuration.basePath()));
    }

    private Server createServer(ServerConfiguration serverConfig) {
        Server server = new Server();
        SelectChannelConnector selectChannelConnector = new SelectChannelConnector();
        selectChannelConnector.setPort(serverConfig.port());
        selectChannelConnector.setHost(serverConfig.bindAddress().getHostAddress());
        server.addConnector(selectChannelConnector);
        server.setThreadPool(new QueuedThreadPool(serverConfig.maxThreadNumber()));
        return server;
    }

    private int portNumber(Server server) {
        return sequence(server.getConnectors()).head().getLocalPort();
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
}
