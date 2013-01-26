package com.googlecode.utterlyidle.bindings.actions;

import com.googlecode.totallylazy.Value;

public class ResourceClass implements ActionMetaData, Value<Class> {
    private final Class value;

    public ResourceClass(Class value) {
        this.value = value;
    }

    @Override
    public Class value() {
        return value;
    }

    public static class constructors{
        public static ResourceClass resourceClass(Class aClass){
            return new ResourceClass(aClass);
        }
    }
}
