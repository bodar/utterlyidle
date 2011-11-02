package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;

public class BasePath extends HierarchicalPath {
    public static BasePath basePath(String path) {
        return new BasePath(path);
    }

    public BasePath(String value) {
        super("/" + UriTemplate.trimSlashes(value) + "/");
    }

}