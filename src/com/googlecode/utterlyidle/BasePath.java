package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;

public class BasePath extends HierarchicalPath {

    public static BasePath basePath(String path) {
        return new BasePath(path);
    }

    public BasePath(String v) {
        super(ensureEndsInSlash(v));
    }

    private static String ensureEndsInSlash(String value) {
        if(!value.endsWith("/")){
            return value + "/";
        }
        return value;
    }
}