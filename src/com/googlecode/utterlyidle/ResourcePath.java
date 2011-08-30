package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.io.HierarchicalPath;

public class ResourcePath extends HierarchicalPath {
    private ResourcePath(String value) {
        super(value);
    }

    public static ResourcePath resourcePath(String value) {
        return new ResourcePath(value);
    }

    public static ResourcePath resourcePathOf(Request request, BasePath basePath) {
        return resourcePathOf(request.uri(), basePath);
    }

    public static ResourcePath resourcePathOf(Uri uri, BasePath basePath) {
        final HierarchicalPath path = hierarchicalPath(uri.path());
        if (path.containedBy(basePath)) {
            return new ResourcePath(path.remove(basePath).toString());
        }
        return new ResourcePath(path.toString());
    }
}
