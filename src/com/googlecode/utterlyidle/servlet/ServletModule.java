package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.AttributeMap;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Engine;
import com.googlecode.utterlyidle.Module;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.ResourcePath;
import com.googlecode.utterlyidle.WebRoot;
import com.googlecode.yadic.Container;

import javax.servlet.ServletContext;

import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.attributeMap;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.webRoot;

public class ServletModule implements Module {
    private final ServletContext context;

    public ServletModule(ServletContext context) {
        this.context = context;
    }

    public Module addPerRequestObjects(Container container) {
        final ServletRequest request = (ServletRequest) container.get(Request.class);
        container.addInstance(BasePath.class, request.basePath());
        container.addInstance(ResourcePath.class, request.resourcePath());
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        container.addActivator(WebRoot.class, webRoot(context));
        container.addActivator(AttributeMap.class, attributeMap(context));
        return this;
    }

    public Module addResources(Engine engine) {
        return this;
    }
}
