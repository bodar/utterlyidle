package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.*;
import com.googlecode.yadic.Container;

import javax.servlet.ServletContext;

import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.*;

public class ServletModule implements Module {
    private final ServletContext context;

    public ServletModule(ServletContext context) {
        this.context = context;
    }

    public Module addPerRequestObjects(Container container) {
        final RequestWithServletStuff request = (RequestWithServletStuff) container.get(Request.class);
        container.
                addInstance(BasePath.class, basePath(request.getRequest())).
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
