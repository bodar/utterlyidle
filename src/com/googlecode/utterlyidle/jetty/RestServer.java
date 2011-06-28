package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import com.googlecode.utterlyidle.servlet.ServletModule;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.thread.QueuedThreadPool;

import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static org.mortbay.jetty.servlet.Context.NO_SESSIONS;

public class RestServer implements com.googlecode.utterlyidle.Server {
    private final Server server;
    private Url url;

    public RestServer(final Application application, final ServerConfiguration configuration) throws Exception {
        server = startApp(application, configuration);
    }

    public void close() throws IOException {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        new RestServer(new HelloWorldApplication(), ServerConfiguration.defaultConfiguration().port(8002));
    }

    private Server startApp(Application application, final ServerConfiguration serverConfig) throws Exception {
        long start = nanoTime();
        Server server = startUpServer(application, serverConfig);
        System.out.println(format("Listening on %s, started Jetty in %s msecs", url, calculateMilliseconds(start, nanoTime())));
        return server;
    }

    private Server startUpServer(Application application, ServerConfiguration serverConfig) throws Exception {
        Server server = createServer(serverConfig);
        Context context = new Context(server, serverConfig.serverUrl().path().toString(), NO_SESSIONS);
        application.add(new ServletModule(context.getServletContext()));
        context.setAttribute(Application.class.getCanonicalName(), application);
        context.addServlet(ApplicationServlet.class, "/*");
        server.start();
        url = serverConfig.port(getPortNumber(server)).serverUrl();
        return server;
    }

    private Server createServer(ServerConfiguration serverConfig) {
        Server server = new Server();
        SocketConnector socketConnector = new SocketConnector();
        socketConnector.setPort(serverConfig.serverUrl().port());
        socketConnector.setHost(serverConfig.bindAddress().getHostAddress());
        server.addConnector(socketConnector);
        server.setThreadPool(new QueuedThreadPool(serverConfig.maxThreadNumber()));
        return server;
    }

    private int getPortNumber(Server server) {
        return sequence(server.getConnectors()).head().getLocalPort();
    }

    public Url getUrl() {
        return url;
    }
}
