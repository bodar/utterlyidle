package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.*;
import com.googlecode.yadic.Container;

import javax.servlet.ServletContext;

import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.attributeMap;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.webRoot;

public class ServletModule implements ApplicationScopedModule {
    private final ServletContext context;

    public ServletModule(ServletContext context) {
        this.context = context;
    }

    public Module addPerApplicationObjects(Container container) {
        container.addActivator(WebRoot.class, webRoot(context));
        container.addActivator(AttributeMap.class, attributeMap(context));
        return this;
    }
}
