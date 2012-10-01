package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;

public class RegisteredResources implements Resources {
    private final List<Binding> bindings = new CopyOnWriteArrayList<Binding>();

    public Resources add(Binding ... bindings) {
        for (Binding binding : bindings) {
            this.bindings.add(binding);
        }
        return this;
    }

    @Override
    public Iterator<Binding> iterator() {
        return bindings.iterator();
    }

    @Override
    public Option<Binding> find(Method method) {
        return sequence(bindings).
                find(where(Binding.extractMethod(), is(method)));

    }
}