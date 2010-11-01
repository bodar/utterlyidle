package com.googlecode.utterlyidle.rendering;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Model extends AbstractMap {
    private final Map<String, List<Object>> values = new HashMap<String, List<Object>>();

    public static Model model() {
        return new Model();
    }

    @Override
    public Set entrySet() {
        return values.entrySet();
    }

    @Override
    public Object put(Object key, Object value) {
        if(!values.containsKey(key.toString())){
            values.put(key.toString(), new ArrayList<Object>());
        }
        values.get(key.toString()).add(value);
        return null;
    }

    public Model add(String key, Object value) {
        put(key, value);
        return this;
    }
}

