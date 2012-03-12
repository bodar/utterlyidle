package com.googlecode.utterlyidle.caching;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheMap extends LinkedHashMap<Request,Response> {
    private final int size;

    public CacheMap(int size) {
        super(size, 0.75f, true);
        this.size = size;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Request,Response> eldest) {
        return size() > size;
    }
}
