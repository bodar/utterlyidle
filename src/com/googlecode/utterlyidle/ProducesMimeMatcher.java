package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNull;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Accept.accept;

public class ProducesMimeMatcher implements Predicate<Request> {
    private final String mimeType;

    public ProducesMimeMatcher(Method method) {
        mimeType = sequence(method.getAnnotation(Produces.class), method.getDeclaringClass().getAnnotation(Produces.class)).
                find(notNull(Produces.class)).map(firstValue()).getOrElse(MediaType.WILDCARD);
    }

    private Callable1<Produces, String> firstValue() {
        return new Callable1<Produces, String>() {
            public String call(Produces produces) throws Exception {
                return sequence(produces.value()).head();
            }
        };
    }

    public boolean matches(Request request) {
        if (request.headers().contains(HttpHeaders.ACCEPT)) {
            return accept(request.headers().getValue(HttpHeaders.ACCEPT)).contains(mimeType);
        }
        return true;
    }

    public float matchQuality(Request request) {
        if (request.headers().contains(HttpHeaders.ACCEPT)) {
            return accept(request.headers().getValue(HttpHeaders.ACCEPT)).quality(mimeType);
        }
        return 1.0f;
    }

    public String mimeType() {
        return mimeType;
    }
}