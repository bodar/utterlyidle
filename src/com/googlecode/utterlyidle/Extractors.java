package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Left;
import com.googlecode.totallylazy.None;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Right;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Some;
import com.googlecode.totallylazy.functions.Callables;

import static com.googlecode.totallylazy.Sequences.one;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Sequences.unzip;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.some;
import static com.googlecode.totallylazy.predicates.Predicates.where;

public class Extractors {
    public static FormParameters extractForm(Binding binding, Object... arguments) {
        return extractParameters(binding, arguments, FormParameters.formParameters());
    }

    public static <T extends Parameters<?>> T extractParameters(final Binding binding, final Object[] arguments, T result) {
        return extractParameters(unzip(binding.parameters()).second(), sequence(arguments), result);
    }

    public static <T extends Parameters<?>> T extractParameters(final Sequence<Option<Parameter>> parameters, final Sequence<Object> values, T input) {
        Parameters<?> result = input;
        for (Pair<Parameter, Object> pair : getParameterAndValue(parameters, values)) {
            if (pair.first() instanceof NamedParameter) {
                NamedParameter parameter = (NamedParameter) pair.first();
                if (parameter.parametersClass().equals(result.getClass())) {
                    for (Object value : asIterable(pair.second())) {
                        result = addAsString(result, parameter, getValue(parameter, value));
                    }
                }
            }
        }
        return cast(result);
    }

    private static Iterable asIterable(Object value) {
        if(value instanceof Either) return one(value);
        if(value instanceof Iterable) return (Iterable) value;
        return one(value);
    }

    private static Parameters<?> addAsString(Parameters<?> result, NamedParameter parameter, String value) {
        if (value != null) {
            result = result.add(parameter.name(), value);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
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
