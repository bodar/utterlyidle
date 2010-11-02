package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Sequences;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.googlecode.totallylazy.Sequences.join;
import static com.googlecode.totallylazy.Sequences.sequence;

public class Model extends AbstractMap<String, List<Object>> {
    private final Map<String, List<Object>> values = new LinkedHashMap<String, List<Object>>();

    public static Model model() {
        return new Model();
    }

    @Override
    public Set<Entry<String, List<Object>>> entrySet() {
        return values.entrySet();
    }

    public Model add(String key, Object value) {
        add(key, sequence(value));
        return this;
    }

    public Model add(String key, Iterable<Object> additionalValues) {
        values.put(key, join(get(key), additionalValues).toList());
        return this;
    }

    @Override
    public List<Object> get(Object key) {
        if (!values.containsKey(key.toString())) {
            values.put(key.toString(), new ArrayList<Object>());
        }
        return values.get(key.toString());
    }

    public List<Object> values(String key) {
        return values.get(key);
    }

    public Object first(String key){
        return sequence(values(key)).head();
    }
}

