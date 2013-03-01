package com.googlecode.utterlyidle.jetty;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import com.googlecode.utterlyidle.servlet.ServletModule;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

import javax.servlet.ServletContext;
import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.nanoTime;
import static org.mortbay.jetty.servlet.Context.NO_SESSIONS;

public class RestServer implements com.googlecode.utterlyidle.Server {
    private final Application application;
    private final ServerConfiguration configuration;
    private Server server;
    private Uri uri;
    private final Callable1<? super Server, ? extends Context> contextCreator;
    private Context context;

    private RestServer(final Application application, final ServerConfiguration configuration, Callable1<? super Server, ? extends Context> contextCreator) throws Exception {
        this.application = application;
        this.configuration = configuration;
        this.contextCreator = contextCreator;
        server = startApp();
    }

    public static RestServer restServer(final Application application, final ServerConfiguration configuration, Callable1<? super Server, ? extends Context> contextCreator) throws Exception {
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
        application(HelloWorldApplication.class).start(defaultConfiguration().port(8002));
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
        servletContext().setAttribute(Application.class.getCanonicalName(), application);

        server.start();
        uri = configuration.port(portNumber(server)).toUrl();
        return server;
    }

    public static Function1<Server, Context> defaultContext(final Application application, final ServerConfiguration configuration) {
        return new Function1<Server, Context>() {
            @Override
            public Context call(Server server) throws Exception {
                Context context = new Context(server, contextPath(configuration.basePath()), NO_SESSIONS);
                application.add(new ServletModule(context.getServletContext()));
                context.addServlet(ApplicationServlet.class, "/*");
                return context;
            }
        };
    }

    private static String contextPath(BasePath basePath) {
        return removeTrailingSlash(basePath.toString());
    }

    private static String removeTrailingSlash(final String value) {
        if(!value.endsWith("/")) return value;
        return value.substring(0, value.length() - 1);
    }

    public static Function1<Server, Context> webXmlContext(final Uri webRoot, final ServerConfiguration configuration) {
        return new Function1<Server, Context>() {
            @Override
            public Context call(Server server) throws Exception {
                return new WebAppContext(server, webRoot.toString(), contextPath(configuration.basePath()));
            }
        };
    }

    private Server createServer(ServerConfiguration serverConfig) {
        Server server = new Server();
        SocketConnector socketConnector = new SocketConnector();
        socketConnector.setPort(serverConfig.port());
        socketConnector.setHost(serverConfig.bindAddress().getHostAddress());
        server.addConnector(socketConnector);
        server.setThreadPool(new QueuedThreadPool(serverConfig.maxThreadNumber()));
        return server;
    }

    private int portNumber(Server server) {
        return sequence(server.getConnectors()).head().getLocalPort();
    }

    public Uri uri() {
        return uri;
    }

    public ServletContext servletContext() {
        return context.getServletContext();
    }
}
