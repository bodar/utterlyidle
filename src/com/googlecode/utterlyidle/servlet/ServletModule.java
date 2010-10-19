package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.ApplicationPath;
import com.googlecode.utterlyidle.AttributeMap;
import com.googlecode.utterlyidle.Engine;
import com.googlecode.utterlyidle.Module;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.ResourcePath;
import com.googlecode.utterlyidle.WebRoot;
import com.googlecode.yadic.Container;

import javax.servlet.ServletContext;

import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.applicationPath;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.attributeMap;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.resourcePath;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.webRoot;

public class ServletModule implements Module {
    private final ServletContext context;

    public ServletModule(ServletContext context) {
        this.context = context;
    }

    public Module addPerRequestObjects(Container container) {
        final RequestWithServletStuff request = (RequestWithServletStuff) container.get(Request.class);
        container.
                addInstance(ApplicationPath.class, applicationPath(request.getRequest())).
                addInstance(ResourcePath.class, resourcePath(request.getRequest()));
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        container.addActivator(WebRoot.class, webRoot(context)).
        addActivator(AttributeMap.class, attributeMap(context));
        return this;
    }

    public Module addResources(Engine engine) {
        return this;
    }
}
