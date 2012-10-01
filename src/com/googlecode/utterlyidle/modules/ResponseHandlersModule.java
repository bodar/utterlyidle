package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.handlers.ResponseHandlers;

public interface ResponseHandlersModule extends Module{
    ResponseHandlers addResponseHandlers(ResponseHandlers handlers) throws Exception;
}
