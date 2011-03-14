package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;

public class ConsumesMimeMatcher implements Predicate<Request> {
    private final String mimeType;

    public ConsumesMimeMatcher(Method method) {
        mimeType = sequence(method.getAnnotation(Consumes.class), method.getDeclaringClass().getAnnotation(Consumes.class)).
                find(notNullValue()).map(firstValue()).getOrElse(MediaType.WILDCARD);
    }

    private Callable1<Consumes, String> firstValue() {
        return new Callable1<Consumes, String>() {
            public String call(Consumes consumes) throws Exception {
                return sequence(consumes.value()).head();
            }
        };
    }

    public boolean matches(Request request) {
        if (mimeType.equals(MediaType.WILDCARD)) {
            return true;
        }
        if (request.headers().contains(HttpHeaders.CONTENT_TYPE)) {
            return request.headers().getValue(HttpHeaders.CONTENT_TYPE).equals(mimeType);
        }
        return true;
    }
}