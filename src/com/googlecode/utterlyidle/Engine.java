package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.Renderers;
import com.googlecode.utterlyidle.handlers.ResponseHandlerRegistry;

public interface Engine extends ActivatorFinder {
    void add(Class resource);

    Renderers renderers();

    ResponseHandlerRegistry responseHandlers();
}
