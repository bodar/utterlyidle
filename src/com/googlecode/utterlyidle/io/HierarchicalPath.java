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
        return sequence(reverse(asList(value.split("/"))));
    }


    private String joinDirectories(Iterable<String> list) {
        return join(list) + "/";
    }

    private String join(Iterable<String> list) {
        return sequence(reverse(list)).toString("/");
    }

    public HierarchicalPath parent() {
        String path = toString();
        if ("".equals(path) || "/".equals(path)) return this;
        return new HierarchicalPath(joinDirectories(sequence(segments()).tail()));
    }

    public HierarchicalPath subDirectory(String name) {
        return new HierarchicalPath(joinDirectories(segments().cons(name)));
    }

    public String file() {
        return segments().head();
    }

    public HierarchicalPath file(String name) {
        return new HierarchicalPath(join(segments().cons(name)));
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

    public static <T> List<T> reverse(Iterable<T> list) {
        List<T> result = list(list);
        Collections.reverse(result);
        return result;
    }

    public static <T> List<T> list(Iterable<T> iterable) {
        List<T> list = new ArrayList<T>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }
}