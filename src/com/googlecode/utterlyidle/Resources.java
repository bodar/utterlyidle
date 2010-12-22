package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.Renderers;
import com.googlecode.utterlyidle.handlers.ResponseHandlerRegistry;

public interface Resources extends ActivatorFinder {
    void add(Class resource);
}
