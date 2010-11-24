package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;

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

    public Parameters remove(String name) {
        values.removeAll(filterByName(name).toList());
        return this;
    }

    public int size() {
        return values.size();
    }

    @SuppressWarnings("unchecked")
    public String getValue(String name) {
        return filterByName(name).headOption().map(second(String.class)).getOrNull();
    }

    private Predicate<String> equalIgnoringCase(final String name) {
        return new Predicate<String>() {
            public boolean matches(String other) {
                return name.equalsIgnoreCase(other);
            }
        };
    }

    public boolean contains(String name) {
        return !filterByName(name).headOption().isEmpty();
    }

    public Iterator<Pair<String, String>> iterator() {
        return values.iterator();
    }

    private Sequence<Pair<String, String>> filterByName(String name) {
        return sequence(values).filter(by(first(String.class), is(equalIgnoringCase(name))));
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
