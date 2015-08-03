package com.googlecode.utterlyidle.jetty.eclipse;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ApplicationBuilder;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static java.lang.String.format;
import static java.lang.System.nanoTime;

public class RestServer implements com.googlecode.utterlyidle.Server {
    protected final Application application;
    protected final ServerConfiguration configuration;
    protected Server server;
    protected Handler handler;
    protected Uri uri;

    protected RestServer(final Application application, final ServerConfiguration configuration) throws Exception {
        this.application = application;
        this.configuration = configuration;
        long start = nanoTime();
        server = createServer(configuration);
        handler = createHandler(server);
        server.start();
        uri = configuration.port(sequence(server.getConnectors()).safeCast(ServerConnector.class).head().getLocalPort()).toUrl();
        System.out.println(format("Listening on %s, started Jetty in %s msecs", uri, calculateMilliseconds(start, nanoTime())));
        Service.functions.start().callConcurrently(application);
    }

    public static RestServer restServer(final Application application, final ServerConfiguration configuration) throws Exception {
        return new RestServer(application, configuration);
    }

    public static void main(String[] args) throws Exception {
        ApplicationBuilder.application(HelloWorldApplication.class).start(defaultConfiguration().port(8002));
    }

    protected Handler createHandler(Server server){
        Handler handler = new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest servletRequest, HttpServletResponse response) throws IOException, ServletException {
                try {
                    ApplicationServlet.transfer(application.handle(ApplicationServlet.request(servletRequest)), response);
                } catch (Exception e) {
                    throw new ServletException(e.getMessage(), e);
                }
            }
        };
        server.setHandler(handler);
        return handler;
    }

    protected Server createServer(ServerConfiguration serverConfig) {
        Server server = new Server(new QueuedThreadPool(serverConfig.maxThreadNumber()));
        ServerConnector serverConnector = new ServerConnector(server, new HttpConnectionFactory());
        serverConnector.setPort(serverConfig.port());
        serverConnector.setHost(serverConfig.bindAddress().getHostAddress());
        server.addConnector(serverConnector);
        return server;
    }

    @Override
    public Application application() {
        return application;
    }

    public Uri uri() {
        return uri;
    }

    public void close() throws IOException {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    static {
        org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
    }
}
