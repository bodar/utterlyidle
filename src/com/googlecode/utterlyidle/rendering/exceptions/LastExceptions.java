package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.Request;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class LastExceptions implements Iterable<StoredException> {

    private final LinkedHashMap<UUID, StoredException> cache;
    private int maximumSize;

    public LastExceptions(final int maximumSize) {
        this.maximumSize = maximumSize;
        this.cache = new LinkedHashMap<UUID, StoredException>(maximumSize) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<UUID, StoredException> eldest) {
                return this.size() > maximumSize;
            }
        };
    }

    public synchronized void put(Date date, Request request, String exception) {
        cache.put(UUID.randomUUID(), new StoredException(date, request, exception));
    }

    @Override
    public Iterator<StoredException> iterator() {
         return Sequences.sequence(cache.values()).memorise().iterator();
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
