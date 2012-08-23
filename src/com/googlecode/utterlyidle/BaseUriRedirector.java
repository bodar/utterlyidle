package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.proxy.Invocation;

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

    @Override
    public Uri resourceUriOf(Invocation invocation) {
        return bindings.find(invocation.method()).
                map(resourceUriOf(invocation.arguments())).
                get();
    }

    @Override
    public Uri resourceUriOf(Binding binding, Object... arguments) {
        return RelativeUriExtractor.relativeUriOf(binding, arguments);
    }

    private Callable1<? super Binding, Uri> resourceUriOf(final Object... arguments) {
        return new Callable1<Binding, Uri>() {
            @Override
            public Uri call(Binding binding) throws Exception {
                return resourceUriOf(binding, arguments);
            }
        };
    }

    private Uri trim(Uri uri) {
        return uri.dropScheme().dropAuthority();
    }

}
