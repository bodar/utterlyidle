package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.PathParameters.pathParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;

public class RequestGenerator {
    private final UriTemplate uriTemplate;
    private final Method method;

    public RequestGenerator(UriTemplate uriTemplate, Method method) {
        this.uriTemplate = uriTemplate;
        this.method = method;
    }

    public Request generate(Object[] arguments) {
        final PathParameters pathParams = pathParameters();
        final HeaderParameters headers = headerParameters();
        final FormParameters formParams = formParameters();
        final QueryParameters queryParams = queryParameters();
        InputStream input = new ByteArrayInputStream(new byte[0]);

        sequence(arguments).zip(sequence(method.getParameterAnnotations())).forEach(new Runnable1<Pair<Object, Annotation[]>>() {
            public void run(Pair<Object, Annotation[]> pair) {
                final Object value = pair.first();
                Sequence<Annotation> annotations = sequence(pair.second());

                annotations.safeCast(PathParam.class).foldLeft(pathParams, new Callable2<Parameters, PathParam, Parameters>() {
                    public Parameters call(Parameters parameters, PathParam param) throws Exception {
                        return parameters.add(param.value(), value.toString());
                    }
                });
                annotations.safeCast(FormParam.class).foldLeft(formParams, new Callable2<Parameters, FormParam, Parameters>() {
                    public Parameters call(Parameters parameters, FormParam param) throws Exception {
                        return parameters.add(param.value(), value.toString());
                    }
                });
                annotations.safeCast(QueryParam.class).foldLeft(queryParams, new Callable2<Parameters, QueryParam, Parameters>() {
                    public Parameters call(Parameters parameters, QueryParam param) throws Exception {
                        return parameters.add(param.value(), value.toString());
                    }
                });
                annotations.safeCast(HeaderParam.class).foldLeft(headers, new Callable2<Parameters, HeaderParam, Parameters>() {
                    public Parameters call(Parameters parameters, HeaderParam param) throws Exception {
                        return parameters.add(param.value(), value.toString());
                    }
                });
            }
        });

        return new Request(null,uriTemplate.generate(pathParams),headers,queryParams,formParams,input);
    }
}