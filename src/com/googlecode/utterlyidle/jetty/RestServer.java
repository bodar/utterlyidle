package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.CloseableCallable;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import com.googlecode.utterlyidle.servlet.ServletModule;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.Closeable;
import java.io.IOException;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static java.lang.String.format;
import static java.lang.System.nanoTime;

public class RestServer implements com.googlecode.utterlyidle.Server {
    private final Server server;
    private final Closeable appCloseable;

    public RestServer(final int port, final BasePath basePath, final CloseableCallable<Application> appActivator) throws Exception {
        appCloseable = appActivator;
        server = startApp(port, basePath, appActivator.call());
    }

    public void close() throws IOException {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
        appCloseable.close();
    }

    public static void main(String[] args) throws Exception {
        new RestServer(8002, basePath("/"), new RestApplicationActivator(new SingleResourceModule(HelloWorld.class)));
    }

    private Server startApp(int port, BasePath basePath, Application application) throws Exception {
        long start = nanoTime();
        Server server = new Server(port);
        Context context = new Context(server, basePath.toString(), Context.NO_SESSIONS);
        application.add(new ServletModule(context.getServletContext()));
        context.setAttribute(Application.class.getCanonicalName(), application);
        context.addServlet(new ServletHolder(new ApplicationServlet()), "/*");
        server.start();
        System.out.println(format("Listening on %s, started Jetty in %s msecs", port, calculateMilliseconds(start, nanoTime())));
        return server;
    }

}
