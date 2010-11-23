package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.googlecode.utterlyidle.Response.response;

public class TestEngine {
    RestApplication application = new RestApplication();

    public TestEngine add(final Class<?> resource) {
        application.add(new SingleResourceModule(resource));
        return this;
    }

    public String handle(RequestBuilder request) throws IOException {
        return handle(request.build());
    }

    public String handle(Request request) throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        handle(request, response(output));
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