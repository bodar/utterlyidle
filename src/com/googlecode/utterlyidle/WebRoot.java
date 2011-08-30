package com.googlecode.utterlyidle;


import com.googlecode.totallylazy.Uri;

public class WebRoot {
    private final Uri uri;

    private WebRoot(Uri uri) {
        this.uri = uri;
    }

    public Uri uri() {
        return uri;
    }

    public static WebRoot webRoot(Uri uri) {
        return new WebRoot(uri);
    }
}

