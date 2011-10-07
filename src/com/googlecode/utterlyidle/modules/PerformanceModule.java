package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.handlers.CacheControlHandler;
import com.googlecode.utterlyidle.handlers.CachePolicy;
import com.googlecode.yadic.Container;

import static com.googlecode.totallylazy.Predicates.or;
import static com.googlecode.utterlyidle.handlers.CachePolicy.cachePolicy;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;

public class PerformanceModule implements RequestScopedModule {
    @Override
    public Module addPerRequestObjects(Container container) throws Exception {
        container.addInstance(CachePolicy.class, cachePolicy(60, or(
                contentType(MediaType.IMAGE_PNG),
                contentType(MediaType.IMAGE_GIF),
                contentType(MediaType.IMAGE_JPEG),
                contentType(MediaType.IMAGE_X_ICON),
                contentType(MediaType.TEXT_JAVASCRIPT),
                contentType(MediaType.TEXT_CSS)
        )));
        container.decorate(HttpHandler.class, EtagHandler.class);
        container.decorate(HttpHandler.class, CacheControlHandler.class);
        return this;
    }
}
