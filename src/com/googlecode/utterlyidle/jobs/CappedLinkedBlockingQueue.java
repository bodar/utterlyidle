package com.googlecode.utterlyidle.jobs;

import java.util.concurrent.LinkedBlockingQueue;

public class CappedLinkedBlockingQueue<T> extends LinkedBlockingQueue<T> {
    public CappedLinkedBlockingQueue(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public boolean add(T t) {
        while (!super.offer(t)) {
            try {
                take();
            } catch (InterruptedException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        return true;
    }
}