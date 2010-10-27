package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Sequences.sequence;

public class HttpMethodExtractor implements Extractor<Method, Option<HttpMethod>> {
    public Option<HttpMethod> extract(Method method) {
        return sequence(method.getAnnotations()).tryPick(new Callable1<Annotation, Option<HttpMethod>>() {
            public Option<HttpMethod> call(Annotation annotation) throws Exception {
                return sequence(annotation.annotationType().getDeclaredAnnotations()).safeCast(HttpMethod.class).headOption();
            }
        });
    }

}