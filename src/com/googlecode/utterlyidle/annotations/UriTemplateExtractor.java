package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Extractor;
import com.googlecode.utterlyidle.UriTemplate;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.UriTemplate.trimSlashes;

public class UriTemplateExtractor implements Extractor<Method, UriTemplate> {
    static UriTemplate uriTemplate(Method method) {
        return new UriTemplateExtractor().extract(method);
    }

    public UriTemplate extract(Method method) {
        Sequence<Path> paths = sequence(method.getDeclaringClass().getAnnotation(Path.class), method.getAnnotation(Path.class));
        return UriTemplate.uriTemplate(paths.filter(notNullValue()).map(getValue()).toString("/"));
    }

    public static Function1<? super Path, String> getValue() {
        return o -> trimSlashes(o.value());
    }
}