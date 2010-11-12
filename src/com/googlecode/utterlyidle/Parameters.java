package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Sequences.sequence;

public class Parameters implements Iterable<Pair<String, String>> {
    private final List<Pair<String, String>> values = new ArrayList<Pair<String, String>>();

    public Parameters add(String name, String value) {
        values.add(pair(name, value));
        return this;
    }

    public int size() {
        return values.size();
    }

    @SuppressWarnings("unchecked")
    public String getValue(String name) {
        return findPair(name).map(second(String.class)).getOrNull();
    }

    @SuppressWarnings("unchecked")
    private Option<Pair<String, String>> findPair(String name) {
        return sequence(values).find(by(first(String.class), is(equalIgnoringCase(name))));
    }

    private Predicate<String> equalIgnoringCase(final String name) {
        return new Predicate<String>() {
            public boolean matches(String other) {
                return name.equalsIgnoreCase(other);
            }
        };
    }

    public boolean contains(String name) {
        return !findPair(name).isEmpty();
    }

    public Iterator<Pair<String, String>> iterator() {
        return values.iterator();
    }

    public static Callable2<Parameters, Pair<String, String>, Parameters> pairIntoParameters() {
        return new Callable2<Parameters, Pair<String, String>, Parameters>() {
            public Parameters call(Parameters result, Pair<String, String> pair) throws Exception {
                return result.add(pair.first(), pair.second());
            }
        };
    }

    @Override
    public String toString() {
        return sequence(values).toString();
    }
}
