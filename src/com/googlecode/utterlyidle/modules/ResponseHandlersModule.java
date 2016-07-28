package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.yadic.Container;

public interface ResponseHandlersModule extends Module{
    ResponseHandlers addResponseHandlers(ResponseHandlers handlers, final Container requestScope) throws Exception;
}
