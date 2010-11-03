package com.googlecode.utterlyidle.proxy;

import com.googlecode.utterlyidle.Redirect;

public class ThreadLocalRedirect extends ThreadLocal<Redirect> {
    @Override
    public Redirect get() {
        final Redirect result = super.get();
        super.set(null);
        return result;
    }

    @Override
    public void set(Redirect value) {
        if (get() != null) {
            throw new UnsupportedOperationException("An unused call already exists, you must use any previous calls before starting another");
        }
        super.set(value);
    }
}
