package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Param.isParam;
import static com.googlecode.utterlyidle.Param.toParam;

public class ParametersExtractor {
    private Sequence<Pair<Object,Annotation[]>> pairs;

    public ParametersExtractor(Method method, Sequence<Object> arguments) {
        pairs = arguments.zip(sequence(method.getParameterAnnotations()));
    }

    public <T extends Parameters> T extract(T parameters, Class<? extends Annotation> aClass){
        if(!argumentOfType(parameters.getClass()).isEmpty()){
            return (T) argumentOfType(parameters.getClass()).get();
        }

        for (Pair<Object, Annotation[]> pair : pairs) {
            final Object value = pair.first();
            final Sequence<Annotation> annotations = sequence(pair.second()).filter(isParam());
            annotations.safeCast(aClass).map(toParam()).foldLeft(parameters, add(value));
        }
        return parameters;
    }

    private <T> Option<T> argumentOfType(Class<T> aClass) {
        return pairs.map(first()).safeCast(aClass).headOption();
    }

    public static Callable2<Parameters, Param, Parameters> add(final Object value) {
        return new Callable2<Parameters, Param, Parameters>() {
            public Parameters call(Parameters parameters, Param param) throws Exception {
                String stringValue = convertToString(value);
                if(stringValue != null){
                    return parameters.add(param.value(), stringValue);
                }
                return parameters;
            }
        };
    }

    private static String convertToString(Object value) {
        if(value == null){
            return null;
        }
        if(value instanceof Left){
            return convertToString(((Left) value).left());
        }
        if(value instanceof Right){
            return convertToString(((Right) value).right());
        }
        if(value instanceof Some){
            return convertToString(((Some) value).get());
        }
        if(value instanceof None){
            return null;
        }
        return value.toString();
    }
}
