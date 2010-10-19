package com.googlecode.utterlyidle.servlet;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class AttributeMap {
    public static Callable<AttributeMap> attributeMap(final ServletContext context){
        return new Callable<AttributeMap>() {
            public AttributeMap call() throws Exception {
                return new AttributeMap() {
                    @Override
                    public AttributeMap put(String name, Object value) {
                        context.setAttribute(name, value);
                        return this;
                    }

                    @Override
                    public <T> T get(String name, Class<T> aClass) {
                        return aClass.cast(context.getAttribute(name));
                    }
                };
            }
        };
    }

    private final Map<String, Object> values = new HashMap<String, Object>();

    public AttributeMap put(String name, Object value) {
        values.put(name, value);
        return this;
    }

    public static AttributeMap attributeMap(){
        return new AttributeMap();
    }

    public <T> T get(String name, Class<T> aClass) {
        return aClass.cast(values.get(name));
    }
}