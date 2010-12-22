package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.ResponseHandlerRegistry;

public interface ResponseHandlersModule extends Module{
    Module addResponseHandlers(ResponseHandlerRegistry registry);
}
