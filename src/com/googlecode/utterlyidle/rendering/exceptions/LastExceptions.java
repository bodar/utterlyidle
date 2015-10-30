package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.Request;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LastExceptions implements Iterable<StoredException> {
    private final Clock clock;
    private final List<StoredException> cache;
    private final int maximumSize;

    public LastExceptions(final LastExceptionsSize maximumSize, final Clock clock) {
        this.maximumSize = maximumSize.value();
        this.clock = clock;
        this.cache = new CopyOnWriteArrayList<StoredException>();
    }

    public synchronized void put(Request request, Exception exception) {
        cache.add(new StoredException(clock.now(), request, exception));
        while (cache.size() > maximumSize) cache.remove(0);
    }

    @Override
    public Iterator<StoredException> iterator() {
        return Sequences.sequence(cache).iterator();
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
