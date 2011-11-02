package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.proxy.Invocation;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.BasePathHandler.toAbsolute;

public class BaseUriRedirector implements Redirector {
    private final BaseUri baseUri;
    private final Bindings bindings;

    public BaseUriRedirector(final BaseUri baseUri, final Bindings bindings) {
        this.baseUri = baseUri;
        this.bindings = bindings;
    }

    @Override
    public Response seeOther(final Invocation invocation) {
        return Responses.seeOther(uriOf(invocation));
    }

    @Override
    public Uri uriOf(final Invocation invocation) {
        return sequence(bindings).
                find(where(method(), is(invocation.method()))).
                map(uri(invocation.arguments())).
                get();
    }

    @Override
    public Uri uriOf(final Binding binding, final Object... arguments) {
        Uri uri = RelativeUriExtractor.relativeUriOf(binding, arguments);
        return toAbsolute(uri, baseUri);
    }

    private Callable1<? super Binding, Uri> uri(final Object... arguments) {
        return new Callable1<Binding, Uri>() {
            @Override
            public Uri call(Binding binding) throws Exception {
                return uriOf(binding, arguments);
            }
        };
    }

    public static Callable1<Binding, Method> method() {
        return new Callable1<Binding, Method>() {
            @Override
            public Method call(final Binding binding) throws Exception {
                return binding.method();
            }
        };
    }
}
