package com.googlecode.utterlyidle;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RegisteredResources implements Resources {
    private final List<Binding> bindings = new CopyOnWriteArrayList<Binding>();

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