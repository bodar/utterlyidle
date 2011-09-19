package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.Value;

import static com.googlecode.totallylazy.Uri.uri;
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
        return new BaseUri(uri(format("http://%s%s", host, basePath))); // TODO: Fix detection of Https
    }

    @Override
    public Uri value() {
        return uri;
    }
}
