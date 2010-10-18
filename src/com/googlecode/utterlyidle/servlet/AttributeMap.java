package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Sequences;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Sequences.sequence;

public class AttributeMap {
    private final Map<String, Object> values = new HashMap<String, Object>();

    public static AttributeMap attributeMap(final ServletContext context){
        return sequence(context.getAttributeNames(), String.class).foldLeft(attributeMap(), new Callable2<AttributeMap, String, AttributeMap>() {
            public AttributeMap call(AttributeMap attributes, String name) throws Exception {
                return attributes.put(name, context.getAttribute(name));
            }
        });
    }

    public AttributeMap put(String name, Object value) {
        values.put(name, value);
        return this;
    }

    public static AttributeMap attributeMap(){
        return new AttributeMap();
    }

    <T> T get(String name, Class<T> aClass) {
        return aClass.cast(values.get(name));
    }
}
