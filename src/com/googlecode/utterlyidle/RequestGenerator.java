package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Runnable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.io.Url;

import javax.ws.rs.*;
import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.Param.isParam;
import static com.googlecode.utterlyidle.Param.toParam;
import static com.googlecode.utterlyidle.PathParameters.pathParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import static com.googlecode.utterlyidle.Request.request;
import static com.googlecode.utterlyidle.io.Url.url;

public class RequestGenerator {
    private final Method method;

    public RequestGenerator(Method method) {
        this.method = method;
    }

    public Request generate(Object[] arguments) {
        return generate(sequence(arguments));
    }
    
    public Request generate(Sequence<Object> arguments) {
        HttpMethod httpMethod = new HttpMethodExtractor().extract(method).get();
        UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);

        final PathParameters paths = pathParameters();
        final HeaderParameters headers = headerParameters();
        final FormParameters forms = formParameters();
        final QueryParameters queries = queryParameters();

        arguments.zip(sequence(method.getParameterAnnotations())).forEach(new Runnable1<Pair<Object, Annotation[]>>() {
            public void run(Pair<Object, Annotation[]> pair) {
                final Object value = pair.first();
                Sequence<Annotation> annotations = sequence(pair.second()).filter(isParam());

                annotations.safeCast(PathParam.class).map(toParam()).foldLeft(paths, add(value));
                annotations.safeCast(FormParam.class).map(toParam()).foldLeft(forms, add(value));
                annotations.safeCast(QueryParam.class).map(toParam()).foldLeft(queries, add(value));
                annotations.safeCast(HeaderParam.class).map(toParam()).foldLeft(headers, add(value));
            }
        });

        return request(httpMethod.value(), url(uriTemplate.generate(paths) + queries.toString()), headers,RequestBuilder.input(forms, null));
    }

    public static Callable2<Parameters, Param, Parameters> add(final Object value) {
        return new Callable2<Parameters, Param, Parameters>() {
            public Parameters call(Parameters parameters, Param param) throws Exception {
                return parameters.add(param.value(), value.toString());
            }
        };
    }
}