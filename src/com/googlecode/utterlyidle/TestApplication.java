package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.dsl.BindingBuilder;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.BindingsModule;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;


public class TestApplication extends RestApplication {
    public TestApplication add(final Class resource) {
        add(new BindingsModule(annotatedClass(resource)));
        return this;
    }

    public TestApplication add(final BindingBuilder bindingBuilder) {
        add(new BindingsModule(bindingBuilder.build()));
        return this;
    }

    public Response handle(RequestBuilder request) throws Exception {
        return handle(request.build());
    }

    public <T> void addResponseHandler(Predicate<? super Pair<Request, Response>> predicate, ResponseHandler responseHandler) {
        applicationScope().get(ResponseHandlers.class).add(predicate, responseHandler);
    }

    public String responseAsString(RequestBuilder request) throws Exception {
        return Strings.toString(handle(request).bytes());
    }

}