package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.proxy.Invocation;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.some;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Sequences.unzip;
import static com.googlecode.utterlyidle.BasePathHandler.asAbsolute;
import static com.googlecode.utterlyidle.BasePathHandler.toAbsolute;
import static com.googlecode.utterlyidle.Responses.seeOther;

public class BaseUriRedirector implements Redirector {
    private final BaseUri baseUri;
    private final Bindings bindings;

    public BaseUriRedirector(final BaseUri baseUri, final Bindings bindings) {
        this.baseUri = baseUri;
        this.bindings = bindings;
    }

    @Override
    public Response redirectTo(final Invocation invocation) {
        return seeOther(uriOf(invocation));
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
        PathParameters parameters = extractParameters(binding, arguments, new PathParameters());
        String path = binding.uriTemplate().generate(parameters).toString();
        QueryParameters query = extractParameters(binding, arguments, new QueryParameters());
        Uri uri = Uri.uri(path + query.toString());
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

    private <T extends Parameters<String, String>> T extractParameters(final Binding binding, final Object[] arguments, T result) {
        return extractParameters(unzip(binding.parameters()).second(), sequence(arguments), result);
    }

    private <T extends Parameters<String, String>> T extractParameters(final Sequence<Option<Parameter>> parameters, final Sequence<Object> values, T result) {
        for (Pair<Parameter, Object> pair : getParameterAndValue(parameters, values)) {
            if (pair.first() instanceof NamedParameter) {
                NamedParameter parameter = (NamedParameter) pair.first();
                if (parameter.parametersClass().equals(result.getClass())) {
                    result.add(parameter.name(), com.googlecode.utterlyidle.annotations.ParametersExtractor.convertToString(pair.second()));
                }
            }
        }
        return result;
    }

    private Sequence<Pair<Parameter, Object>> getParameterAndValue(final Sequence<Option<Parameter>> parameters, final Sequence<Object> arguments) {
        return parameters.zip(arguments).
                filter(where(Callables.<Option<Parameter>>first(), is(some(Parameter.class)))).
                map(Callables.<Option<Parameter>, Object, Parameter>first(Callables.<Parameter>value()));

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
