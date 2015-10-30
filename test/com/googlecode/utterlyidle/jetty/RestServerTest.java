package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.ServerContract;

public class RestServerTest extends ServerContract<RestServer> {
    @Override
    protected Class<RestServer> server() throws Exception {
        return RestServer.class;
    }

//    @Test
//    public void canSwapApplicationAfterStart() throws Exception {
//        ServletContext context = server.servletContext();
//        Application newApp = new HelloWorldApplication(BasePath.basePath("foo"));
//        ApplicationContext.setApplication(context, newApp);
//        assertThat(newApp, sameInstance(ApplicationContext.getApplication(context, null)));
//    }
}
