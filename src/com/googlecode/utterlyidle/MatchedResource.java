package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.GenericType;
import com.googlecode.totallylazy.Unchecked;

public class MatchedResource implements GenericType<Object> {
    private final Class<Object> aClass;

    public MatchedResource(Class<?> aClass) {
        this.aClass = Unchecked.cast(aClass);
    }

    @Override
    public Class<Object> forClass() {
        return aClass;
    }
}
