package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.predicates.WherePredicate;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;

public class TestApplication extends RestApplication {
    public TestApplication add(final Class resource) {
        add(new SingleResourceModule(resource));
        return this;
    }

    public String handle(RequestBuilder request) throws Exception {
        return responseFor(request).output().toString();
    }

    public Response responseFor(RequestBuilder request) throws Exception {
        Response response = MemoryResponse.response();
        handle(request, response);
        return response;
    }

    public void handle(RequestBuilder request, Response response) throws Exception {
        handle(request.build(), response);
    }

    public <T> void addRenderer(Class<T> customClass, Renderer<T> renderer) {
        engine().renderers().add(where(entity(), is(instanceOf(customClass))), renderer);
    }

    public <T> void addResponseHandler(Predicate<? super Pair<Request, Response>> predicate, ResponseHandler responseHandler) {
        engine().responseHandlers().add(predicate, responseHandler);
    }
}