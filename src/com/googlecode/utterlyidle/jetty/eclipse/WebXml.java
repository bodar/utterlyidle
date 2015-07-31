package com.googlecode.utterlyidle.jetty.eclipse;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ServerConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebXml {
    public static RestServer restServer(final Application application, final ServerConfiguration configuration, Uri webRoot) throws Exception {
        return RestServer.restServer(application, configuration, webXmlContext(webRoot, configuration));
    }

    public static Function1<Server, ServletContextHandler> webXmlContext(final Uri webRoot, final ServerConfiguration configuration) {
        return new Function1<Server, ServletContextHandler>() {
            @Override
            public ServletContextHandler call(Server server) throws Exception {
                return new WebAppContext(server, webRoot.toString(), RestServer.contextPath(configuration.basePath()));
            }
        };
    }
}
