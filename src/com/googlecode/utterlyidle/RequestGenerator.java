package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.proxy.Invocation;

public interface RequestGenerator {
    public Request requestFor(Invocation<Object, String> invocation);
    public Request requestFor(Binding binding, Object... arguments);
}
