package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Pair;

import java.util.*;

import static com.googlecode.totallylazy.Sequences.sequence;

public class Parameters implements Iterable<Pair<String, List<String>>> {

    private final Map<String, List<String>> values = new HashMap<String, List<String>>();

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

}
