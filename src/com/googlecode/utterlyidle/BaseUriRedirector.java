package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.http.Uri;
import com.googlecode.totallylazy.annotations.multimethod;
import com.googlecode.totallylazy.multi;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.totallylazy.proxy.MethodInvocation;

import java.util.NoSuchElementException;

import static com.googlecode.utterlyidle.BasePathHandler.toFullyQualified;

public class BaseUriRedirector implements Redirector {
    private final BaseUri baseUri;
    private Bindings bindings;

    public BaseUriRedirector(final BaseUri baseUri, final Bindings bindings) {
        this.baseUri = baseUri;
        this.bindings = bindings;
    }

    @Override
    public Response seeOther(final Uri relativeUri) {
        return Responses.seeOther(toFullyQualified(relativeUri, baseUri));
    }

    @Override
    public Response seeOther(final Invocation invocation) {
        return Responses.seeOther(uriOf(invocation));
    }

    @Override
    public Response seeOther(final Binding binding, final Object... arguments) {
        return Responses.seeOther(uriOf(binding, arguments));
    }

    @Override
    public Uri uriOf(final Invocation invocation) {
        return toFullyQualified(resourceUriOf(invocation), baseUri);
    }

    @Override
    public Uri uriOf(final Binding binding, final Object... arguments) {
        return toFullyQualified(resourceUriOf(binding, arguments), baseUri);
    }

    @Override
    public Uri absoluteUriOf(Uri relativeUri) {
        return trim(toFullyQualified(relativeUri, baseUri));
    }

    @Override
    public Uri absoluteUriOf(Invocation invocation) {
        return trim(uriOf(invocation));
    }

    @Override
    public Uri absoluteUriOf(Binding binding, Object... arguments) {
        return trim(uriOf(binding, arguments));
    }

    private multi multi;
    @Override
    public Uri resourceUriOf(Invocation invocation) {
        if(multi == null) multi = new multi(){};
        return multi.method(invocation);
    }

    @multimethod
    public Uri resourceUriOf(MethodInvocation invocation) {
        Option<Binding> methodBinding = bindings.find(invocation.method());
        if(methodBinding.isEmpty())
            throw new NoSuchElementException(String.format("No binding found for %s", invocation.method()));
        return methodBinding.
                map(resourceUriOf(invocation.arguments())).
                get();
    }

    @Override
    public Uri resourceUriOf(Binding binding, Object... arguments) {
        return RelativeUriExtractor.relativeUriOf(binding, arguments);
    }

    private Function1<? super Binding, Uri> resourceUriOf(final Object... arguments) {
        return binding -> resourceUriOf(binding, arguments);
    }

    private Uri trim(Uri uri) {
        return uri.dropScheme().dropAuthority();
    }

}
