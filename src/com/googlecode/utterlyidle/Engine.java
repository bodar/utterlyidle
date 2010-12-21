package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.Renderers;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;

public interface Engine extends ActivatorFinder {
    void add(Class resource);

    Renderers renderers();

    ResponseHandlers responseHandlers();
}
