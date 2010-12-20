package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.handlers.RendererHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static com.googlecode.utterlyidle.MemoryResponse.response;

public class TestEngine implements Engine {
    RestApplication application = new RestApplication();

    public void add(final Class resource) {
        application.add(new SingleResourceModule(resource));
    }

    public String handle(RequestBuilder request) throws Exception {
        return handle(request.build());
    }

    public String handle(Request request) throws Exception {
        OutputStream output = new ByteArrayOutputStream();
        handle(request, response(output));
        return output.toString();
    }

    public void handle(RequestBuilder request, Response response) throws Exception {
        handle(request.build(), response);
    }

    public void handle(Request request, Response response) throws Exception {
        application.handle(request, response);
    }

    public <T> void addRenderer(Class<T> customClass, Renderer<T> renderer) {
        renderers().add(Predicates.assignableTo(customClass), renderer);
    }

    public RendererHandler renderers() {
        return application.engine().renderers();
    }

    public ResponseHandlers responseHandlers() {
        return application.engine().responseHandlers();
    }

    public Either<MatchFailure, HttpMethodActivator> findActivator(Request request) {
        return application.engine().findActivator(request);
    }
}