package com.googlecode.utterlyidle.sitemesh;

import java.util.Arrays;
import java.util.Collection;

public class PropertyMap extends UnsupportedMap<String, Object>{
    private final Property property;

    public PropertyMap(Property property) {
        this.property = property;
    }

    @Override
    public boolean containsKey(Object key) {
        return property.hasChild(key.toString());
    }

    @Override
    public Object get(Object key) {
        return  new PropertyMap(property.getChild(key.toString()));
    }

    @Override
    public String toString() {
        return property.getValue();
    }

    @Override
    public Collection<Object> values() {
        return Arrays.<Object>asList(property.getValue());
    }
}
