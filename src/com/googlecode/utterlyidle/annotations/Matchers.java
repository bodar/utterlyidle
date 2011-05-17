package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.FormParameters;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Hidden;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.NamedParameter;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.PathParameters;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.UriTemplate;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.annotations.Param.toParam;

public class Matchers {
    public static Sequence<String> producesMimeMatcher(Method method) {
        return extractMediaType(method, Produces.class);
    }

    public static Sequence<String> consumesMimeMatcher(Method method) {
        return extractMediaType(method, Consumes.class);
    }

    @SuppressWarnings("unchecked")
    private static Sequence<String> extractMediaType(Method method, Class annotation) {
        return sequence(method.getAnnotation(annotation), method.getDeclaringClass().getAnnotation(annotation)).
                find(notNullValue()).map(toParam()).map(asSequence()).getOrElse(sequence(MediaType.WILDCARD));
    }

    private static Callable1<Param, Sequence<String>> asSequence() {
        return new Callable1<Param, Sequence<String>>() {
            public Sequence<String> call(Param param) throws Exception {
                return sequence(param.<String[]>value());
            }
        };
    }

    public static Binding binding(String httpMethod, Method method) {
        UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);
        return new Binding(method, uriTemplate, httpMethod, consumesMimeMatcher(method), producesMimeMatcher(method), extractTypesAndNames(method), new PriorityExtractor().extract(method), hidden(method));
    }

    private static boolean hidden(Method method) {
         return method.getAnnotation(Hidden.class) != null;
    }

    private static Sequence<Pair<Type, Option<NamedParameter>>> extractTypesAndNames(Method method) {
        return sequence(method.getGenericParameterTypes()).zip(convertToAnnotationsToNamedParameter(method));
    }

    private static Map<Class<? extends Annotation>, Class<? extends Parameters<String, String>>> supportedAnnotations = new HashMap<Class<? extends Annotation>, Class<? extends Parameters<String, String>>> (){{
        put(QueryParam.class, QueryParameters.class);
        put(FormParam.class, FormParameters.class);
        put(PathParam.class, PathParameters.class);
        put(HeaderParam.class, HeaderParameters.class);
        put(CookieParam.class, CookieParameters.class);
    }};

    private static Sequence<Option<NamedParameter>> convertToAnnotationsToNamedParameter(final Method method) {
        return sequence(method.getParameterAnnotations()).map(new Callable1<Annotation[], Option<NamedParameter>>() {
            public Option<NamedParameter> call(Annotation[] annotations) throws Exception {
                for (final Param param : sequence(annotations).map(toParam())) {
                    Class<? extends Annotation> key = param.annotation().annotationType();
                    if(supportedAnnotations.containsKey(key)){
                        return some(new NamedParameter(param.<String>value(), supportedAnnotations.get(key)));
                    }
                }
                return none();
            }
        });
    }
}