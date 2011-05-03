package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.HttpMethodExtractor;
import com.googlecode.utterlyidle.annotations.Matchers;
import com.googlecode.utterlyidle.dsl.BindingBuilder;

import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RegisteredResources implements Resources {
    private final List<Binding> bindings = new ArrayList<Binding>();

    public void add(Class annotatedClass) {
        for (final Method method : annotatedClass.getMethods()) {
            for (final HttpMethod httpMethod : new HttpMethodExtractor().extract(method)) {
                add(Matchers.binding(httpMethod.value(), method));
            }
        }
    }

    public void add(Binding binding) {
        bindings.add(binding);
    }

    public void add(BindingBuilder binding) {
        bindings.add(binding.build());
    }

    public Iterable<Binding> bindings() {
        return bindings;
    }
}