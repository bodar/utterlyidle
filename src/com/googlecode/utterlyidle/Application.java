package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.yadic.Container;

public interface Application extends HttpHandler {
    Container applicationScope();

    Container createRequestScope();

    Application add(Module module);

    Resources resources();

    ResponseHandlers responseHandlers();
}
