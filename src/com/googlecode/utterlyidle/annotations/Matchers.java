package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.annotations.Param.toParam;

public class Matchers {
    public static ProducesMimeMatcher producesMimeMatcher(Method method) {
        return new ProducesMimeMatcher(extractMediaType(method, Produces.class));
    }

    public static ConsumesMimeMatcher consumesMimeMatcher(Method method) {
        return new ConsumesMimeMatcher(extractMediaType(method, Consumes.class));
    }

    @SuppressWarnings("unchecked")
    private static String extractMediaType(Method method, Class annotation) {
        return sequence(method.getAnnotation(annotation), method.getDeclaringClass().getAnnotation(annotation)).
                find(notNullValue()).map(toParam()).map(firstValue()).getOrElse(MediaType.WILDCARD);
    }

    private static Callable1<Param, String> firstValue() {
        return new Callable1<Param, String>() {
            public String call(Param param) throws Exception {
                return sequence(param.<String[]>value()).head();
            }
        };
    }

    public static HttpMethodActivator httpMethodActivator(String httpMethod, Method method, Application application) {
        UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);
        return new HttpMethodActivator(method, uriTemplate, httpMethod, consumesMimeMatcher(method), producesMimeMatcher(method), new ArgumentsExtractor(method, uriTemplate, application), new PriorityExtractor().extract(method));
    }
}