package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.io.Uri;

import static com.googlecode.totallylazy.Sequences.flatten;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;

public class RelativeUriExtractor {
    public static Uri relativeUriOf(Binding binding, Object... arguments) {
        PathParameters parameters = Extractors.extractParameters(binding, arguments, PathParameters.pathParameters());
        String path = binding.uriTemplate().generate(parameters).toString();
        QueryParameters query = Extractors.extractParameters(binding, arguments, QueryParameters.queryParameters());
        return Uri.uri(path + extractParametersFromQueryArgument(arguments, query).toString());
    }

    private static QueryParameters extractParametersFromQueryArgument(Object[] arguments, QueryParameters sourceParameters) {
        return queryParameters(flatten(sequence(arguments).safeCast(QueryParameters.class)).join(sequence(sourceParameters)));
    }

}
