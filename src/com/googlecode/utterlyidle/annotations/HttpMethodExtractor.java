package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Extractor;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Sequences.sequence;

public class HttpMethodExtractor implements Extractor<Method, Option<HttpMethod>> {
    public static Option<HttpMethod> httpMethod(Method method) {
        return new HttpMethodExtractor().extract(method);
    }

    public Option<HttpMethod> extract(Method method) {
        return sequence(method.getAnnotations()).
                tryPick(annotation -> sequence(annotation.annotationType().getDeclaredAnnotations()).
                        safeCast(HttpMethod.class).
                        headOption());
    }

}
