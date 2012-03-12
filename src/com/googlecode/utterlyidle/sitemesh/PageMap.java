package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import java.util.HashMap;
import java.util.Map;

public class PageMap extends UnsupportedMap {

    private final HttpHandler httpHandler;
    private final InternalRequestMarker internalRequestMarker;
    private Map<String, PropertyMap> cache = new HashMap<String, PropertyMap>();

    public PageMap(HttpHandler httpHandler, InternalRequestMarker internalRequestMarker) {
        this.httpHandler = httpHandler;
        this.internalRequestMarker = internalRequestMarker;
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
        return cache.get(path);
    }

    private void getAndCache(String url) {
        try {
            Response response = httpHandler.handle(internalRequestMarker.markAsInternal(RequestBuilder.get(url).build()));
            if(!response.status().equals(Status.OK)) {
                return;
            }
            cache.put(url, new PropertyMapParser().parse(new String(response.bytes())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
