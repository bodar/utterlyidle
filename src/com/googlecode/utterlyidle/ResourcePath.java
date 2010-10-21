package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;

public class ResourcePath extends HierarchicalPath {
    private ResourcePath(String value) {
        super(value);
    }

    public static ResourcePath resourcePath(String value) {
        return new ResourcePath(value);
    }
}
