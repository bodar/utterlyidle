package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.Url;

public class WebRoot {
    private final Url url;

    private WebRoot(Url url) {
        this.url = url;
    }

    public Url getUrl() {
        return url;
    }

    public static WebRoot webRoot(Url url) {
        return new WebRoot(url);
    }
}

