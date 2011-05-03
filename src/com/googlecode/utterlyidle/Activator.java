package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

import java.lang.reflect.Method;

public interface Activator {
    Response activate(Resolver resolver, Request request, Application application) throws Exception;

    Method method();

    HttpSignature httpSignature();
}
