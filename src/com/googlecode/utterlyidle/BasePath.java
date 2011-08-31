package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;

import java.util.regex.Pattern;

public class BasePath extends HierarchicalPath {
    private static final Pattern trim = Pattern.compile("^(/)?(.*?)(/)?$");
    public static BasePath basePath(String path) {
        return new BasePath(path);
    }

    public BasePath(String value) {
        super("/" + trimSlashes(value) + "/");
    }

    private static String trimSlashes(String value) {
        return trim.matcher(value).replaceAll("$2");
    }
}