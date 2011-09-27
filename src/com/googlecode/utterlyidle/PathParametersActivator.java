package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;

import java.util.concurrent.Callable;

public class PathParametersActivator implements Callable<PathParameters> {
    private final HierarchicalPath path;
    private final UriTemplate uriTemplate;

    public PathParametersActivator(HierarchicalPath path, UriTemplate uriTemplate) {
        this.path = path;
        this.uriTemplate = uriTemplate;
    }

    public PathParameters call() throws Exception {
        return uriTemplate.extract(path.toString());
    }
}
