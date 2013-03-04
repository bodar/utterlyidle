package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerContract;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.servlet.ApplicationContext;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.applet.AppletContext;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

public class RestServerTest extends ServerContract<RestServer> {
    @Override
    protected Class<RestServer> server() throws Exception {
        return RestServer.class;
    }

    @Test
    public void canSwapApplicationAfterStart() throws Exception {
        ServletContext context = server.servletContext();
        Application newApp = new HelloWorldApplication(BasePath.basePath("foo"));
        ApplicationContext.setApplication(context, newApp);
        assertThat(newApp, sameInstance(ApplicationContext.getApplication(context, null)));
    }
}
