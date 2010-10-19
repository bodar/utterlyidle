package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;

public final class ApplicationPath extends HierarchicalPath {
    private ApplicationPath(String value) {
        super(value);
    }

    public static ApplicationPath applicationPath(String value) {
        return new ApplicationPath(value);
    }
}
