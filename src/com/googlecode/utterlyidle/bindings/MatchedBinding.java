package com.googlecode.utterlyidle.bindings;

import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.Binding;

public class MatchedBinding implements Value<Binding> {
    private final Binding value;

    public MatchedBinding(Binding value) {
        this.value = value;
    }

    @Override
    public Binding value() {
        return value;
    }
    public static class constructors{
        public static MatchedBinding matchedBinding(Binding binding){
            return new MatchedBinding(binding);
        }
    }
}
