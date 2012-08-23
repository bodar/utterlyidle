package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.proxy.Invocation;

public interface RequestGenerator {
    public Request requestFor(final Invocation invocation);
    public Request requestFor(final Binding binding, final Object... arguments);
}
