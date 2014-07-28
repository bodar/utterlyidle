package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.AttributeMap;
import com.googlecode.utterlyidle.WebRoot;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;

import javax.servlet.ServletContext;

import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.attributeMap;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.webRoot;

public class ServletModule implements ApplicationScopedModule, RequestScopedModule {
    private final ServletContext context;

    public ServletModule(ServletContext context) {
        this.context = context;
    }

    public Container addPerApplicationObjects(Container container) {
        return container.
                addActivator(WebRoot.class, webRoot(context)).
                addActivator(AttributeMap.class, attributeMap(context));
    }

    public Container addPerRequestObjects(Container container) {
        return container;
    }
}
