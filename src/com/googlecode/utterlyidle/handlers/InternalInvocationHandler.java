package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.RequestGenerator;
import com.googlecode.utterlyidle.Response;

public class InternalInvocationHandler implements InvocationHandler {

    private Application application;
    private RequestGenerator requestGenerator;

    public InternalInvocationHandler(final Application application, final RequestGenerator requestGenerator) {
        this.application = application;
        this.requestGenerator = requestGenerator;
    }

    @Override
    public Response handle(final Invocation invocation) throws Exception {
        return application.handle(requestGenerator.requestFor(invocation));
    }
}
