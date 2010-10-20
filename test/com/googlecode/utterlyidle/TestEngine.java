package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TestEngine {
    RestEngine engine = new RestEngine();
    Container container = new SimpleContainer();

    public TestEngine add(Class<?> resource) {
        engine.add(resource);
        container.add(resource);
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
        engine.handle(container, request, response);
    }

    public <T> void addRenderer(Class<T> customClass, Renderer<T> renderer) {
        engine.addRenderer(customClass, renderer);
    }
}
