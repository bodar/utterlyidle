package com.googlecode.utterlyidle.jetty.eclipse;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.servlet.ApplicationServlet;
import com.googlecode.utterlyidle.servlet.ServletModule;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.ServletContext;

public class ServletContextRestServer extends RestServer {
    protected ServletContextRestServer(final Application application, final ServerConfiguration configuration) throws Exception {
        super(application, configuration);
    }

    public static ServletContextRestServer restServer(final Application application, final ServerConfiguration configuration) throws Exception {
        return new ServletContextRestServer(application, configuration);
    }

    @Override
    protected Handler createHandler(final Server server) {
        ServletContextHandler context = new ServletContextHandler(server, contextPath(configuration.basePath()), false, false);
        application.add(new ServletModule(context.getServletContext()));
        context.addServlet(ApplicationServlet.class, "/*");
        context.setAttribute(Application.class.getCanonicalName(), application);
        return context;
    }

    static String contextPath(BasePath basePath) {
        return removeTrailingSlash(basePath.toString());
    }

    protected static String removeTrailingSlash(final String value) {
        if (!value.endsWith("/")) return value;
        return value.substring(0, value.length() - 1);
    }

    public ServletContext servletContext() {
        return ((ServletContextHandler) handler).getServletContext();
    }
}
