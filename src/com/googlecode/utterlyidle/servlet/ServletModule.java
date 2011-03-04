package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.AttributeMap;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.WebRoot;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;

import javax.servlet.ServletContext;

import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.attributeMap;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.webRoot;

public class ServletModule implements ApplicationScopedModule, RequestScopedModule {
    private final ThreadLocal<BasePath> basePath = new ThreadLocal<BasePath>();
    private final ServletContext context;

    public ServletModule(ServletContext context) {
        this.context = context;
    }

    public Module addPerApplicationObjects(Container container) {
        container.addActivator(WebRoot.class, webRoot(context));
        container.addActivator(AttributeMap.class, attributeMap(context));
        return this;
    }

    public Module addPerRequestObjects(Container container) {
        container.addInstance(BasePath.class, basePath.get());
        return this;
    }
}
