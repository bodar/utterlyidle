package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;

public class RelativeUriExtractor {
    public static Uri relativeUriOf(Binding binding, Object... arguments) {
        PathParameters parameters = Extractors.extractParameters(binding, arguments, new PathParameters());
        String path = binding.uriTemplate().generate(parameters).toString();
        QueryParameters query = Extractors.extractParameters(binding, arguments, new QueryParameters());
        return Uri.uri(path + query.toString());
    }


}
