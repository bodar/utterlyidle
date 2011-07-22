package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;

import java.util.HashMap;
import java.util.Map;

public class PageMap extends UnsupportedMap {

    private final HttpHandler httpHandler;
    private Map<String, PropertyMap> cache = new HashMap<String, PropertyMap>();

    public PageMap(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public boolean containsKey(Object url) {
        if (!(url instanceof String)) {
            return false;
        }

        String path = (String) url;

        if (!cache.containsKey(path)) {
            getAndCache(path);
        }
        return true;

    }

    @Override
    public Object get(Object url) {
        String path = (String) url;
        return cache.get(path);

    }

    private void getAndCache(String url) {
        try {
            Response response = httpHandler.handle(RequestBuilder.get(url).build());
            cache.put(url, new PropertyMapParser().parse(new String(response.bytes())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}