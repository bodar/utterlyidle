package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.HttpMethodExtractor;
import com.googlecode.utterlyidle.annotations.Matchers;
import com.googlecode.utterlyidle.dsl.ActivatorBuilder;

import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class AnnotatedResources implements Resources {
    private final List<Activator> activators = new ArrayList<Activator>();
    private final Application application;

    public AnnotatedResources(Application application) {
        this.application = application;
    }

    public void add(Class resource) {
        for (final Method method : resource.getMethods()) {
            for (final HttpMethod httpMethod : new HttpMethodExtractor().extract(method)) {
                add(Matchers.httpMethodActivator(httpMethod.value(), method, application));
            }
        }
    }

    public void add(Activator activator) {
        activators.add(activator);
    }

    public void add(ActivatorBuilder activator) {
        activators.add(activator.build(application));
    }

    public Iterable<Activator> activators() {
        return activators;
    }
}