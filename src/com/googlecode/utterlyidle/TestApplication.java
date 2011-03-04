package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;

import static com.googlecode.utterlyidle.BasePath.basePath;

public class TestApplication extends RestApplication {
    public TestApplication() {
        add(new RequestInstanceModule(basePath("/")));
    }

    public TestApplication add(final Class resource) {
        add(new SingleResourceModule(resource));
        return this;
    }

    public Response handle(RequestBuilder request) throws Exception {
        return handle(request.build());
    }

    public <T> void addResponseHandler(Predicate<? super Pair<Request, Response>> predicate, ResponseHandler responseHandler) {
        responseHandlers().add(predicate, responseHandler);
    }

    public String responseAsString(RequestBuilder request) throws Exception {
        return Strings.toString(handle(request).bytes());
    }

}