package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.annotations.multimethod;
import com.googlecode.totallylazy.multi;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.totallylazy.proxy.MethodInvocation;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.FormParameters;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.NamedParameter;
import com.googlecode.utterlyidle.Parameter;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.PathParameters;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.RelativeUriExtractor;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.annotations.HttpMethodExtractor.httpMethod;
import static com.googlecode.utterlyidle.annotations.Param.param;
import static com.googlecode.utterlyidle.annotations.Param.toParam;
import static com.googlecode.utterlyidle.annotations.UriTemplateExtractor.uriTemplate;
import static com.googlecode.utterlyidle.annotations.View.constructors.view;
import static com.googlecode.utterlyidle.bindings.actions.InvokeResourceMethod.constructors.invokeResourceMethod;

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

    private static multi multi;
    public static Uri relativeUriOf(final Invocation invocation) {
        if(multi == null) multi = new multi(){};
        return multi.method(invocation);
    }

    @multimethod
    public static Uri relativeUriOf(final MethodInvocation invocation) {
        return RelativeUriExtractor.relativeUriOf(binding(invocation.method()).get(), invocation.arguments());
    }

    private static Callable1<HttpMethod, Binding> asBinding(final Method method) {
        return new Callable1<HttpMethod, Binding>() {
            public Binding call(HttpMethod httpMethod) throws Exception {
                return new Binding(invokeResourceMethod(method), uriTemplate(method), httpMethod.value(), consumesMimeMatcher(method), producesMimeMatcher(method), extractTypesAndNames(method), new PriorityExtractor().extract(method), hidden(method), extractView(method));
            }
        };
    }

    private static View extractView(final Method method) {
        return view(option(method.getAnnotation(View.class)).map(Param.<String>toValue()).getOrElse(method.getName()));
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

    static boolean hidden(Method method) {
        return sequence(method.getAnnotation(Hidden.class), method.getDeclaringClass().getAnnotation(Hidden.class)).exists(notNullValue());
    }

    private static Sequence<Pair<Type, Option<Parameter>>> extractTypesAndNames(Method method) {
        return sequence(method.getGenericParameterTypes()).zip(namedParameters(method));
    }

    private static Map<Class<? extends Annotation>, Class<? extends Parameters<String, String, ?>>> supportedAnnotations = new HashMap<Class<? extends Annotation>, Class<? extends Parameters<String, String, ?>>>() {{
        put(QueryParam.class, QueryParameters.class);
        put(FormParam.class, FormParameters.class);
        put(PathParam.class, PathParameters.class);
        put(HeaderParam.class, HeaderParameters.class);
        put(CookieParam.class, CookieParameters.class);
    }};

    private static Sequence<Option<Parameter>> namedParameters(final Method method) {
        return sequence(method.getParameterAnnotations()).map(new Callable1<Annotation[], Option<Parameter>>() {
            public Option<Parameter> call(Annotation[] annotations) throws Exception {
                for (final Annotation annotation : annotations) {
                    Class<? extends Annotation> key = annotation.annotationType();
                    if (supportedAnnotations.containsKey(key)) {
                        Param param = param(annotation);
                        return Option.<Parameter>some(new NamedParameter(param.<String>value(), supportedAnnotations.get(key), defaultValue(annotations)));
                    }
                }
                return none();
            }
        });
    }

    private static Option<String> defaultValue(Annotation[] annotations) {
        return sequence(annotations).find(instanceOf(DefaultValue.class)).map(Param.<String>toValue());
    }

    public static class functions {
        public static Mapper<Method, Option<Binding>> binding = new Mapper<Method, Option<Binding>>() {
            @Override
            public Option<Binding> call(Method method) throws Exception {
                return AnnotatedBindings.binding(method);
            }
        };
    }
}
