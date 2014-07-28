package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.yadic.Container;

import java.io.Closeable;

public interface Application extends HttpHandler, Closeable, Service {
    Container applicationScope();

    <T> T usingRequestScope(Callable1<Container, T> callable);

    <T> T usingArgumentScope(Request request, Callable1<Container, T> callable);

    Application add(Module module);
}
