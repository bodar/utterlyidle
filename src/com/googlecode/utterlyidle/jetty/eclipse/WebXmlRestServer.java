package com.googlecode.utterlyidle.jetty.eclipse;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ServerConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebXmlRestServer extends ServletContextRestServer {
    private final Uri webRoot;

    protected WebXmlRestServer(final Application application, final ServerConfiguration configuration, final Uri webRoot) throws Exception {
        super(application, configuration);
        this.webRoot = webRoot;
    }

    public static RestServer restServer(final Application application, final ServerConfiguration configuration, Uri webRoot) throws Exception {
        return new WebXmlRestServer(application, configuration, webRoot);
    }

    @Override
    protected Handler createHandler(final Server server) {
        return new WebAppContext(server, webRoot.toString(), contextPath(configuration.basePath()));
    }
}
