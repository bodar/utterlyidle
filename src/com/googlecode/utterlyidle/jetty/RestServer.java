package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import com.googlecode.utterlyidle.servlet.ServletModule;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class RestServer implements com.googlecode.utterlyidle.Server {
    private final Server server;

    public RestServer(final int port, final BasePath basePath, final Application application) throws Exception {
        server = new Server(port);
        Context context = new Context(server, basePath.toString(), Context.NO_SESSIONS);
        application.add(new ServletModule(context.getServletContext()));
        context.setAttribute(Application.class.getCanonicalName(), application);
        context.addServlet(new ServletHolder(new ApplicationServlet()), "/*");
        start();
    }

    public com.googlecode.utterlyidle.Server start() throws Exception {
        server.start();
        return this;
    }

    public com.googlecode.utterlyidle.Server stop() throws Exception {
        server.stop();
        return this;
    }
}
