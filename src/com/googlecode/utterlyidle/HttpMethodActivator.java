package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequences;

import java.lang.reflect.Method;

public class HttpMethodActivator implements Activator {
    private final HttpSignature httpSignature;
    private final Method method;

    public HttpMethodActivator(HttpSignature httpSignature, Method method) {
        this.httpSignature = httpSignature;
        this.method = method;
    }

    public HttpSignature httpSignature() {
        return httpSignature;
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + Sequences.sequence(method.getGenericParameterTypes()).toString("(", ", ", ")");
    }

    public Method method() {
        return method;
    }
}