package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Debug.debugging;

public class PageMap extends UnsupportedMap {
    private final HttpHandler httpHandler;
    private Map<String, PropertyMap> cache = new HashMap<String, PropertyMap>();

    public PageMap(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public boolean containsKey(Object url) {
        if (url == null) {
            return false;
        }

        String path = url.toString();

        if (!cache.containsKey(path)) {
            getAndCache(path);
        }
        return true;
    }

    @Override
    public Object get(Object url) {
        String path = url.toString();
        if (!cache.containsKey(path)) {
            getAndCache(path);
        }

        return cache.get(path);
    }

    private void getAndCache(String url) {
        try {
            Response response = httpHandler.handle(RequestBuilder.get(url).build());
            if(!response.status().isSuccessful()) {
                if(debugging()) System.err.printf("Failed to include '%s' received '%s'%n", url, response.status());
                return;
            }
            cache.put(url, new PropertyMapParser().parse(response.entity().toString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
