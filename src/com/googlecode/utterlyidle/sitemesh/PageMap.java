package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
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
        return url != null && get(url) != null;
    }

    @Override
    public Object get(Object key) {
        String url = key.toString();
        return cache.computeIfAbsent(url, n -> {
            try {
                Response response = httpHandler.handle(Request.get(url));
                if(!response.status().isSuccessful()) {
                    if(debugging()) System.err.printf("Failed to include '%s' received '%s'%n", url, response.status());
                    return null;
                }
                return new PropertyMapParser().parse(response.entity().toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
