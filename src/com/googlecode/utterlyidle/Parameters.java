package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Pair;

import java.util.*;

import static com.googlecode.totallylazy.Sequences.sequence;

public class Parameters implements Iterable<Pair<String, List<String>>> {
    private final Map<String, List<String>> values = new LinkedHashMap<String, List<String>>();

    public Parameters add(String name, String value) {
        if (!values.containsKey(name)) {
            values.put(name, new ArrayList<String>());
        }
        values.get(name).add(value);
        return this;
    }

    public int size() {
        return values.size();
    }

    public String getValue(String name) {
        return values.containsKey(name) ? values.get(name).get(0) : null;
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public Iterator<Pair<String, List<String>>> iterator() {
        return sequence(values.entrySet()).map(Callables.<String, List<String>>entryToPair()).iterator();
    }

    public static <T extends Parameters> Callable2<T, String, T> addParameter(final String name) {
        return new Callable2<T, String, T>() {
            public T call(T result, String value) throws Exception {
                return (T) result.add(name, value);
            }
        };
    }

    public static Callable2<Parameters, Pair<String, String>, Parameters> pairIntoParameters() {
        return new Callable2<Parameters, Pair<String, String>, Parameters>() {
            public Parameters call(Parameters result, Pair<String, String> pair) throws Exception {
                return result.add(pair.first(), pair.second());
            }
        };
    }

}
