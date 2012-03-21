package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;

import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.some;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Sequences.unzip;

public class Extractors {
    public static FormParameters extractForm(Binding binding, Object... arguments) {
        return  extractParameters(binding, arguments, new FormParameters());
    }

    public static <T extends Parameters<String, String>> T extractParameters(final Binding binding, final Object[] arguments, T result) {
        return extractParameters(unzip(binding.parameters()).second(), sequence(arguments), result);
    }

    public static <T extends Parameters<String, String>> T extractParameters(final Sequence<Option<Parameter>> parameters, final Sequence<Object> values, T result) {
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

    static Sequence<Pair<Parameter, Object>> getParameterAndValue(final Sequence<Option<Parameter>> parameters, final Sequence<Object> arguments) {
        return parameters.zip(arguments).
                filter(where(Callables.<Option<Parameter>>first(), is(some(Parameter.class)))).
                map(Callables.<Option<Parameter>, Object, Parameter>first(Callables.<Parameter>value()));

    }

    static String getValue(NamedParameter parameter, Object value) {
        if (value == null) {
            return parameter.defaultValue().get();
        }
        return convertToString(value);
    }

    static String convertToString(Object value) {
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
