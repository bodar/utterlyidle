package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.handlers.*;
import com.googlecode.yadic.Container;

import static com.googlecode.utterlyidle.handlers.CachePolicy.cachePolicy;
import static com.googlecode.utterlyidle.handlers.GZipPolicy.gZipPolicy;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;

public class PerformanceModule implements RequestScopedModule {
    @Override
    public Container addPerRequestObjects(Container container) throws Exception {
        return container.
                addInstance(GZipPolicy.class, gZipPolicy().
                        add(contentType(MediaType.TEXT_JAVASCRIPT)).
                        add(contentType(MediaType.TEXT_CSS))).
                decorate(HttpHandler.class, GzipHandler.class).
                addInstance(CachePolicy.class, cachePolicy(60).
                        add(contentType(MediaType.IMAGE_PNG)).
                        add(contentType(MediaType.IMAGE_GIF)).
                        add(contentType(MediaType.IMAGE_JPEG)).
                        add(contentType(MediaType.IMAGE_X_ICON)).
                        add(contentType(MediaType.TEXT_JAVASCRIPT)).
                        add(contentType(MediaType.TEXT_CSS))).
                decorate(HttpHandler.class, CacheControlHandler.class).
                decorate(HttpHandler.class, EtagHandler.class);
    }
}
