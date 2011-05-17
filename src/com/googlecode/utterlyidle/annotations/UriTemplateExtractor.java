package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Extractor;
import com.googlecode.utterlyidle.UriTemplate;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;

public class UriTemplateExtractor implements Extractor<Method, UriTemplate> {
    public UriTemplate extract(Method method) {
        Sequence<Path> paths = sequence(method.getDeclaringClass().getAnnotation(Path.class), method.getAnnotation(Path.class));
        return UriTemplate.uriTemplate(paths.filter(notNullValue()).map(getValue()).toString("/"));
    }

    public static Callable1<? super Path, String> getValue() {
        return new Callable1<Path, String>() {
            public String call(Path o) throws Exception {
                return o.value();
            }
        };
    }
}