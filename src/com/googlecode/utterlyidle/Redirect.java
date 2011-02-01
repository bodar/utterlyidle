package com.googlecode.utterlyidle;

public class Redirect {
    private final String value;

    private Redirect(String value) {
        this.value = value;
    }

    public static Redirect redirect(String value) {
        return new Redirect(value);
    }

    public String location() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Redirect && ((Redirect) o).location().equals(location());
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
