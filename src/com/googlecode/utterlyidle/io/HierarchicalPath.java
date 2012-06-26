package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

import static com.googlecode.totallylazy.Sequences.empty;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.numbers.Numbers.equalTo;
import static com.googlecode.totallylazy.numbers.Numbers.greaterThan;

public class HierarchicalPath implements Path, Comparable<HierarchicalPath> {
    private static final String DUPLICATE_SEPARATORS = "\\/+";
    private final String value;

    public static HierarchicalPath hierarchicalPath(String value) {
        return new HierarchicalPath(value);
    }

    protected HierarchicalPath(String value) {
        this.value = value.replaceAll(DUPLICATE_SEPARATORS, "/");
    }

    public Sequence<String> segments() {
        if(value.equals("")){
            return empty();
        }
        if(value.equals("/")){
            return sequence("");
        }
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

    public HierarchicalPath subDirectory(Path name) {
        return subDirectory(name.toString());
    }

    public String file() {
        return segments().last();
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

    public int compareTo(HierarchicalPath other) {
        return value.compareTo(other.value);
    }

    public boolean containedBy(HierarchicalPath basePath) {
        if (greaterThan(basePath.segments().size(), segments().size())){
            return false;
        }

        return equalTo(
                firstConsecutiveCommonSegments(basePath).size(),
                basePath.segments().size());
    }

    private Sequence<Pair<String, String>> firstConsecutiveCommonSegments(HierarchicalPath basePath) {
        return basePath.segments().zip(segments()).takeWhile(segmentIsEqual());
    }

    private Predicate<Pair<String, String>> segmentIsEqual() {
        return new Predicate<Pair<String, String>>() {
            public boolean matches(Pair<String, String> pathSegment) {
                return pathSegment.first().equals(pathSegment.second());
            }
        };
    }

    public HierarchicalPath remove(HierarchicalPath base) {
        if (!containedBy(base)) {
            return this;
        }
        Sequence<String> remainingSegments = segments().drop(firstConsecutiveCommonSegments(base).size());
        return new HierarchicalPath(join(remainingSegments));
    }

}