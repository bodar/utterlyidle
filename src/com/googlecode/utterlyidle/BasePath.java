package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;

public class BasePath extends HierarchicalPath {

    public static BasePath basePath(String path) {
        return new BasePath(path);
    }

    public BasePath(ApplicationPath applicationPath, ResourcePath resourcePath) {
        this(applicationPath.subDirectory(resourcePath).toString());
    }

    protected BasePath(String v) {
        super(v);
    }
}