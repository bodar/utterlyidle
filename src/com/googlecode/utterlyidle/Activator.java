package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.Method;

public interface Activator {
    Response activate(Resolver resolver, Request request, Application application) throws Exception;

    Method method();

    HttpSignature httpSignature();

    public static class ExtensionMethods {
        public static Callable1<Activator, HttpSignature> signature() {
            return new Callable1<Activator, HttpSignature>() {
                public HttpSignature call(Activator activator) throws Exception {
                    return activator.httpSignature();
                }
            };
        }
    }
}
