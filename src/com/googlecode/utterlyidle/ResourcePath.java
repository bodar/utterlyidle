package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;
import com.googlecode.utterlyidle.io.Url;

public class ResourcePath extends HierarchicalPath {
    private ResourcePath(String value) {
        super(value);
    }

    public static ResourcePath resourcePath(String value) {
        return new ResourcePath(value);
    }

    public static ResourcePath resourcePathOf(Request request, BasePath basePath) {
        return resourcePathOf(request.url(), basePath);
    }

    public static ResourcePath resourcePathOf(Url url, BasePath basePath) {
        final HierarchicalPath path = url.path();
        if (path.containedBy(basePath)) {
            return new ResourcePath(path.remove(basePath).toString());
        }
        return new ResourcePath(path.toString());
    }
}
