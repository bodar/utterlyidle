package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public interface Application extends RequestHandler{
    Container applicationScope();
    Container createRequestScope(Request request);
    Application add(Module module);

    Engine engine();
}
