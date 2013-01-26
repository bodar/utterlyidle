package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;

import java.lang.reflect.Method;

public interface Bindings extends Iterable<Binding> {
    @Deprecated
    /**
     * Bindings are being decoupled from just invoking methods.
     *
     * Do this instead:
     *
     * sequence(bindings).find(Binding.functions.isForMethod(method))
     */
    public Option<Binding> find(Method method);
}
