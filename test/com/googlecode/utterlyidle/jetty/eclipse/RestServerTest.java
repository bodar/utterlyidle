package com.googlecode.utterlyidle.jetty.eclipse;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.ServerContract;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.jetty.eclipse.RestServer;
import com.googlecode.utterlyidle.servlet.ApplicationContext;
import org.junit.Test;

import javax.servlet.ServletContext;

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
