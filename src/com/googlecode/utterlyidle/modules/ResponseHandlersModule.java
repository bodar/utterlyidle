package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.handlers.ResponseHandlerRegistry;

public interface ResponseHandlersModule extends Module{
    Module addResponseHandlers(ResponseHandlerRegistry registry);
}
