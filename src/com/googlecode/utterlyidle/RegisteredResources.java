package com.googlecode.utterlyidle;

import java.util.ArrayList;
import java.util.List;

public class RegisteredResources implements Resources {
    private final List<Binding> bindings = new ArrayList<Binding>();

    public void add(Binding ... bindings) {
        for (Binding binding : bindings) {
            this.bindings.add(binding);
        }
    }

    public Iterable<Binding> bindings() {
        return bindings;
    }
}