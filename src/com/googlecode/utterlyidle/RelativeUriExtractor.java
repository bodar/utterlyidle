package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Left;
import com.googlecode.totallylazy.None;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Right;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Some;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.proxy.Invocation;

import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.some;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Sequences.unzip;

public class RelativeUriExtractor {
    public static Uri relativeUriOf(Binding binding, Object... arguments) {
        PathParameters parameters = extractParameters(binding, arguments, new PathParameters());
        String path = binding.uriTemplate().generate(parameters).toString();
        QueryParameters query = extractParameters(binding, arguments, new QueryParameters());
        return Uri.uri(path + query.toString());
    }

    private static <T extends Parameters<String, String>> T extractParameters(final Binding binding, final Object[] arguments, T result) {
        return extractParameters(unzip(binding.parameters()).second(), sequence(arguments), result);
    }

    private static <T extends Parameters<String, String>> T extractParameters(final Sequence<Option<Parameter>> parameters, final Sequence<Object> values, T result) {
        for (Pair<Parameter, Object> pair : getParameterAndValue(parameters, values)) {
            if (pair.first() instanceof NamedParameter) {
                NamedParameter parameter = (NamedParameter) pair.first();
                if (parameter.parametersClass().equals(result.getClass())) {
                    String value = getValue(parameter, pair.second());
                    if (value != null) {
                        result.add(parameter.name(), value);
                    }
                }
            }
        }
        return result;
    }

    private static Sequence<Pair<Parameter, Object>> getParameterAndValue(final Sequence<Option<Parameter>> parameters, final Sequence<Object> arguments) {
        return parameters.zip(arguments).
                filter(where(Callables.<Option<Parameter>>first(), is(some(Parameter.class)))).
                map(Callables.<Option<Parameter>, Object, Parameter>first(Callables.<Parameter>value()));

    }

    private static String getValue(NamedParameter parameter, Object value) {
        if (value == null) {
            return parameter.defaultValue().get();
        }
        return convertToString(value);
    }

    private static String convertToString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Left) {
            return convertToString(((Left) value).left());
        }
        if (value instanceof Right) {
            return convertToString(((Right) value).right());
        }
        if (value instanceof Some) {
            return convertToString(((Some) value).get());
        }
        if (value instanceof None) {
            return null;
        }
        return value.toString();
    }


}
