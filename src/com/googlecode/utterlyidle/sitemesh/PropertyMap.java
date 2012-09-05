package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Iterators;
import com.googlecode.totallylazy.Sequences;
import org.sitemesh.content.ContentProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class PropertyMap extends UnsupportedMap<String, Object>{
    private final ContentProperty property;

    public PropertyMap(ContentProperty property) {
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

    public PropertyMap getPropertyMap(String key) {
        return (PropertyMap) get(key);
    }

    @Override
    public int size() {
        return Sequences.size(property.getChildren());
    }
}

