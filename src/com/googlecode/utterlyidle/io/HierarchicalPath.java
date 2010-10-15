package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.Sequence;
import static com.googlecode.totallylazy.Sequences.sequence;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;

public class HierarchicalPath implements Path {
    private static final String DUPLICATE_SEPARATORS = "\\/+";
    private final String value;

    public static HierarchicalPath hierarchicalPath(String value) {
        return new HierarchicalPath(value);
    }

    protected HierarchicalPath(String value) {
        this.value = value.replaceAll(DUPLICATE_SEPARATORS, "/");
    }

    private Sequence<String> segments() {
        return sequence(value.split("/"));
    }

    private String joinDirectories(Sequence<String> list) {
        return join(list) + "/";
    }

    private String join(Sequence<String> list) {
        return list.toString("/");
    }

    public HierarchicalPath parent() {
        String path = toString();
        if ("".equals(path) || "/".equals(path)) return this;
        return new HierarchicalPath(joinDirectories(segments().reverse().tail().reverse()));
    }

    public HierarchicalPath subDirectory(String name) {
        return new HierarchicalPath(joinDirectories(segments().add(name)));
    }

    public String file() {
        return segments().head();
    }

    public HierarchicalPath file(String name) {
        return new HierarchicalPath(join(segments().add(name)));
    }

    public String toString() {
        return value;
    }

    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HierarchicalPath that = (HierarchicalPath) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }
}