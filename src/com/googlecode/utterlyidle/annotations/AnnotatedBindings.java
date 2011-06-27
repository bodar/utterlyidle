package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import javax.xml.bind.Binder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.annotations.HttpMethodExtractor.httpMethod;
import static com.googlecode.utterlyidle.annotations.Param.toParam;
import static com.googlecode.utterlyidle.annotations.UriTemplateExtractor.uriTemplate;

public class AnnotatedBindings {
    public static Binding[] annotatedClass(Class<?> aClass) {
        return sequence(aClass.getMethods()).flatMap(new Callable1<Method, Iterable<Binding>>() {
            public Iterable<Binding> call(Method method) throws Exception {
                return binding(method);
            }
        }).toArray(Binding.class);
    }

    public static Option<Binding> binding(final Method method) {
        return httpMethod(method).map(asBinding(method));
    }

    private static Callable1<HttpMethod, Binding> asBinding(final Method method) {
        return new Callable1<HttpMethod, Binding>() {
            public Binding call(HttpMethod httpMethod) throws Exception {
                return new Binding(method, uriTemplate(method), httpMethod.value(), consumesMimeMatcher(method), producesMimeMatcher(method), extractTypesAndNames(method), new PriorityExtractor().extract(method), hidden(method));
            }
        };
    }

    private static Sequence<String> producesMimeMatcher(Method method) {
        return extractMediaType(method, Produces.class);
    }

    private static Sequence<String> consumesMimeMatcher(Method method) {
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

    private static boolean hidden(Method method) {
        return method.getAnnotation(Hidden.class) != null;
    }

    private static Sequence<Pair<Type, Option<NamedParameter>>> extractTypesAndNames(Method method) {
        return sequence(method.getGenericParameterTypes()).zip(namedParameters(method));
    }

    private static Map<Class<? extends Annotation>, Class<? extends Parameters<String, String>>> supportedAnnotations = new HashMap<Class<? extends Annotation>, Class<? extends Parameters<String, String>>>() {{
        put(QueryParam.class, QueryParameters.class);
        put(FormParam.class, FormParameters.class);
        put(PathParam.class, PathParameters.class);
        put(HeaderParam.class, HeaderParameters.class);
        put(CookieParam.class, CookieParameters.class);
    }};

    private static Sequence<Option<NamedParameter>> namedParameters(final Method method) {
        return sequence(method.getParameterAnnotations()).map(new Callable1<Annotation[], Option<NamedParameter>>() {
            public Option<NamedParameter> call(Annotation[] annotations) throws Exception {
                for (final Param param : sequence(annotations).map(toParam())) {
                    Class<? extends Annotation> key = param.annotation().annotationType();
                    if (supportedAnnotations.containsKey(key)) {
                        return some(new NamedParameter(param.<String>value(), supportedAnnotations.get(key)));
                    }
                }
                return none();
            }
        });
    }
}
