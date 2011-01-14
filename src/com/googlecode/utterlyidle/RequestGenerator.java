package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;

import javax.ws.rs.*;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
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
        final HttpMethod httpMethod = new HttpMethodExtractor().extract(method).get();
        final UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);

        final ParametersExtractor parametersExtractor = new ParametersExtractor(method, arguments);
        final PathParameters paths = parametersExtractor.extract(pathParameters(), PathParam.class );
        final HeaderParameters headers = parametersExtractor.extract(headerParameters(), HeaderParam.class );
        final FormParameters forms = parametersExtractor.extract(formParameters(), FormParam.class);
        final QueryParameters queries = parametersExtractor.extract(queryParameters(), QueryParam.class);

        return request(httpMethod.value(), url(uriTemplate.generate(paths) + queries.toString()), headers, RequestBuilder.input(forms, null));
    }
}