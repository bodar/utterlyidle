package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.proxy.Invocation;

import static com.googlecode.utterlyidle.Extractors.extractForm;

public class BindingsRequestGenerator implements RequestGenerator {
    private final Redirector redirector;
    private Bindings bindings;

    public BindingsRequestGenerator(final Redirector redirector, final Bindings bindings) {
        this.redirector = redirector;
        this.bindings = bindings;
    }

    @Override
    public Request requestFor(final Invocation invocation) {
        return requestFor(bindings.find(invocation.method()).get(), invocation.arguments());
    }

    @Override
    public Request requestFor(final Binding binding, final Object... arguments) {
        return  new RequestBuilder(binding.httpMethod(), redirector.uriOf(binding, arguments)).withForms(extractForm(binding, arguments)).build();
    }

}
