package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Binding.functions.isForMethod;

public class RegisteredResources implements Resources {
    private final List<Binding> bindings = new CopyOnWriteArrayList<Binding>();

    public Resources add(Binding... bindings) {
        Collections.addAll(this.bindings, bindings);
        return this;
    }

    @Override
    public Iterator<Binding> iterator() {
        return bindings.iterator();
    }

    @Override
    public Option<Binding> find(Method method) {
        return sequence(bindings)
                .find(isForMethod(method));
    }
}