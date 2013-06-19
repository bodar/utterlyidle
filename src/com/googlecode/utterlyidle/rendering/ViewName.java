package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Eq;
import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.annotations.multimethod;

public class ViewName extends Eq implements Value<String> {
    private final String value;

    private ViewName(final String value) {
        this.value = value;
    }

    public static ViewName viewName(final String value) {
        return new ViewName(value);
    }

    @Override
    public String value() {
        return value;
    }

    @multimethod
    public boolean equals(final ViewName other) {
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
