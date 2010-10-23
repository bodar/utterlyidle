package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicates;
import com.googlecode.yadic.Container;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TestEngine {
    RestApplication application = new RestApplication();

    public TestEngine() {
        application.add(new Module() {
            public Module addPerRequestObjects(Container container) {
                container.addInstance(BasePath.class, new BasePath(""));
                return this;
            }

            public Module addPerApplicationObjects(Container container) {
                return this;
            }

            public Module addResources(Engine engine) {
                return this;
            }
        });
    }

    public TestEngine add(final Class<?> resource) {
        application.add(new TestModule(resource));
        return this;
    }

    public String handle(RequestBuilder request) throws IOException {
        return handle(request.build());
    }

    public String handle(Request request) throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        Response response = Response.response(output);
        handle(request, response);
        response.flush();
        return output.toString();
    }

    public void handle(RequestBuilder request, Response response) {
        handle(request.build(), response);
    }

    public void handle(Request request, Response response) {
        application.handle(request, response);
    }

    public <T> void addRenderer(Class<T> customClass, Renderer<T> renderer) {
        application.engine().renderers().add(Predicates.assignableTo(customClass), renderer);
    }
}