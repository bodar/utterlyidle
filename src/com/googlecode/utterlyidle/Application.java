package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public interface Application extends RequestHandler{
    Container createRequestScope();
    Application add(Module module);
    Container applicationScope();
}
