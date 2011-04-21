package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.ParametersExtractor;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
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
        return new HttpMethodActivator(method, uriTemplate, httpMethod, consumesMimeMatcher(method), producesMimeMatcher(method), new ParametersExtractor(uriTemplate, application, extractTypesAndNames(method)), new PriorityExtractor().extract(method));
    }

    private static Sequence<Pair<Type, Option<NamedParameter>>> extractTypesAndNames(Method method) {
        return sequence(method.getGenericParameterTypes()).zip(convertToAnnotationsToNamedParameter(method));
    }

    private static Sequence<Option<NamedParameter>> convertToAnnotationsToNamedParameter(final Method method) {
        return sequence(method.getParameterAnnotations()).map(new Callable1<Annotation[], Option<NamedParameter>>() {
            public Option<NamedParameter> call(Annotation[] annotations) throws Exception {
                for (final Param param : sequence(annotations).map(toParam())) {
                    if (param.annotation() instanceof QueryParam) {
                        return some(new NamedParameter(param.<String>value(), QueryParameters.class));
                    }
                    if (param.annotation() instanceof FormParam) {
                        return some(new NamedParameter(param.<String>value(), FormParameters.class));

                    }
                    if (param.annotation() instanceof PathParam) {
                        return some(new NamedParameter(param.<String>value(), PathParameters.class));
                    }
                    if (param.annotation() instanceof HeaderParam) {
                        return some(new NamedParameter(param.<String>value(), HeaderParameters.class));
                    }
                    if (param.annotation() instanceof CookieParam) {
                        return some(new NamedParameter(param.<String>value(), CookieParameters.class));
                    }
                }
                return none();
            }
        });
    }

}