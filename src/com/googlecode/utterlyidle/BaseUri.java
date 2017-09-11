package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.io.Uri;

import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.HOST;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_HOST;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_PROTO;
import static java.lang.String.format;

public class BaseUri extends Value.Type<Uri> implements Value<Uri> {
    public BaseUri(Uri uri) {
        super(uri);
    }

    public static BaseUri baseUri(Uri uri){
        return new BaseUri(uri);
    }

    public static BaseUri baseUri(String uri){
        return new BaseUri(Uri.uri(uri));
    }

    public static BaseUri baseUri(Request request, BasePath basePath) {
        String host = request.headers().valueOption(X_FORWARDED_HOST).getOrElse(request.headers().getValue(HOST));
        if (host == null) {
            return new BaseUri(uri(basePath.toString()));
        }

        String scheme = request.headers().valueOption(X_FORWARDED_PROTO).getOrElse("http");

        return new BaseUri(uri(format("%s://%s%s", scheme, host, basePath)));
    }

    @Override
    public String toString() {
        return value().toString();
    }
}
