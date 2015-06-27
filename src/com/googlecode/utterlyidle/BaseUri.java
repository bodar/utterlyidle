package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.http.Uri;
import com.googlecode.totallylazy.Value;

import static com.googlecode.totallylazy.http.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_PROTO;
import static java.lang.String.format;

public class BaseUri implements Value<Uri> {
    private final Uri uri;

    public BaseUri(Uri uri) {
        this.uri = uri;
    }

    public static BaseUri baseUri(Uri uri){
        return new BaseUri(uri);
    }

    public static BaseUri baseUri(String uri){
        return new BaseUri(Uri.uri(uri));
    }

    public static BaseUri baseUri(Request request, BasePath basePath) {
        String host = request.headers().getValue(HttpHeaders.HOST);
        if (host == null) {
            return new BaseUri(uri(basePath.toString()));
        }

        String scheme = request.headers().valueOption(X_FORWARDED_PROTO).getOrElse("http");

        return new BaseUri(uri(format("%s://%s%s", scheme, host, basePath)));
    }

    @Override
    public Uri value() {
        return uri;
    }

    @Override
    public String toString() {
        return uri.toString();
    }
}
