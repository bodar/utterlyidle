package com.googlecode.utterlyidle;

import java.util.HashMap;
import java.util.Map;

public class AttributeMap {
    private final Map<String, Object> values = new HashMap<String, Object>();

    public AttributeMap put(String name, Object value) {
        values.put(name, value);
        return this;
    }

    public static AttributeMap attributeMap() {
        return new AttributeMap();
    }

    public <T> T get(String name, Class<T> aClass) {
        return aClass.cast(values.get(name));
    }
}
