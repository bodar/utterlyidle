package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.*;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;

public class Matchers {
    public static ProducesMimeMatcher producesMimeMatcher(Method method) {
        return new ProducesMimeMatcher(sequence(method.getAnnotation(Produces.class), method.getDeclaringClass().getAnnotation(Produces.class)).
                find(notNullValue()).map(firstValue()).getOrElse(MediaType.WILDCARD));
    }

    private static Callable1<Produces, String> firstValue() {
        return new Callable1<Produces, String>() {
            public String call(Produces produces) throws Exception {
                return sequence(produces.value()).head();
            }
        };
    }

    public static HttpMethodActivator httpMethodActivator(String httpMethod, Method method, Application application) {
        UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);
        return new HttpMethodActivator(method, uriTemplate, httpMethod, new ConsumesMimeMatcher(method), producesMimeMatcher(method), new ArgumentsExtractor(method, uriTemplate, application), new PriorityExtractor().extract(method));
    }
}