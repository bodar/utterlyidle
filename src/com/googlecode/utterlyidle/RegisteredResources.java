package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegisteredResources implements Resources {
    private final List<Binding> bindings = new ArrayList<Binding>();

    public void add(Binding ... bindings) {
        for (Binding binding : bindings) {
            this.bindings.add(binding);
        }
    }

    @Override
    public Iterator<Binding> iterator() {
        return bindings.iterator();
    }
}