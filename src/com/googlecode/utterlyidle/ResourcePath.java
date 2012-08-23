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

    public static ResourcePath resourcePathOf(Request request) {
        return resourcePathOf(request.uri());
    }

    public static ResourcePath resourcePathOf(Uri uri) {
        return new ResourcePath(uri.path());
    }
}
