package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.RendererHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;

public interface Engine extends ActivatorFinder {
    void add(Class resource);

    RendererHandler renderers();

    ResponseHandlers responseHandlers();
}
