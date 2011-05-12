package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.CloseableCallable;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import com.googlecode.utterlyidle.servlet.ServletModule;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.ServerConfiguration.serverConfiguration;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static org.mortbay.jetty.servlet.Context.NO_SESSIONS;

public class RestServer implements com.googlecode.utterlyidle.Server {
    private final Server server;
    private final Closeable appClosable;
    private Url url;

    public RestServer(final CloseableCallable<Application> applicationActivator) throws Exception {
        this(applicationActivator, serverConfiguration());
    }

    public RestServer(final CloseableCallable<Application> applicationActivator, final ServerConfiguration configuration) throws Exception {
        appClosable = applicationActivator;
        server = startApp(applicationActivator, configuration);
    }

    public void close() throws IOException {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
        appClosable.close();
    }

    public static void main(String[] args) throws Exception {
        new RestServer(new RestApplicationActivator(new SingleResourceModule(HelloWorld.class)), serverConfiguration().withPortNumber(8002));
    }

    private Server startApp(CloseableCallable<Application> applicationActivator, final ServerConfiguration serverConfig) throws Exception {
        long start = nanoTime();
        Server server = startUpServer(applicationActivator.call(), serverConfig);
        System.out.println(format("Listening on %s, started Jetty in %s msecs", getPortNumber(server), calculateMilliseconds(start, nanoTime())));
        return server;
    }

    private Server startUpServer(Application application, ServerConfiguration serverConfig) throws Exception {
        Server server = new Server();
        SocketConnector socketConnector = new SocketConnector();
        socketConnector.setPort(serverConfig.portNumber());
        socketConnector.setHost(serverConfig.bindAddress().getHostAddress());
        server.addConnector(socketConnector);
        server.setThreadPool(new QueuedThreadPool(serverConfig.maxThreadNumber()));
        Context context = new Context(server, serverConfig.basePath().toString(), NO_SESSIONS);
        application.add(new ServletModule(context.getServletContext()));
        context.setAttribute(Application.class.getCanonicalName(), application);
        context.addServlet(new ServletHolder(new ApplicationServlet()), "/*");
        server.start();
        url = Url.url(format("http://localhost:%s%s", getPortNumber(server), serverConfig.basePath()));
        return server;
    }

    private int getPortNumber(Server server) {
        return sequence(server.getConnectors()).head().getLocalPort();
    }

    public Url getUrl() {
        return url;
    }
}
