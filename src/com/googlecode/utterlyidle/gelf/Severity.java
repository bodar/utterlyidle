package com.googlecode.utterlyidle.gelf;

import com.googlecode.totallylazy.Value;

import static com.googlecode.totallylazy.Sequences.sequence;

public enum Severity implements Value<Integer> {
    Emergency("emerg"),
    Alert("alert"),
    Critical("crit"),
    Error("err"),
    Warning("warning"),
    Notice("notice"),
    Informational("info"),
    Debug("debug");

    private final String keyword;

    Severity(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    @Override
    public Integer value() {
        return ordinal();
    }

    public static Severity parse(String name){
        return sequence(Severity.values()).
                find(value -> value.name().equalsIgnoreCase(name) || value.keyword().equalsIgnoreCase(name)).
                get();
    }
}
