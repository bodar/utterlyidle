package com.googlecode.utterlyidle.proxy;

public class ThreadLocalRedirect extends ThreadLocal<String> {
    @Override
    public String get() {
        final String result = super.get();
        super.set(null);
        return result;
    }

    @Override
    public void set(String value) {
        if (get() != null) {
            throw new UnsupportedOperationException("An unused call already exists, you must use any previous calls before starting another");
        }
        super.set(value);
    }
}
