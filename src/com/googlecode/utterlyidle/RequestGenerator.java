package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.annotations.FormParam;
import com.googlecode.utterlyidle.annotations.HeaderParam;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.annotations.HttpMethodExtractor;
import com.googlecode.utterlyidle.annotations.ParametersExtractor;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.QueryParam;
import com.googlecode.utterlyidle.annotations.UriTemplateExtractor;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.PathParameters.pathParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import static com.googlecode.utterlyidle.Requests.request;

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

        return request(httpMethod.value(), Uri.uri(uriTemplate.generate(paths) + queries.toString()), headers, RequestBuilder.input(forms, null));
    }
}