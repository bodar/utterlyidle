package com.googlecode.utterlyidle;

import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class AnnotatedResources implements Resources {
    private final List<HttpMethodActivator> activators = new ArrayList<HttpMethodActivator>();
    private final Application application;

    public AnnotatedResources(Application application) {
        this.application = application;
    }

    public void add(Class resource) {
        for (final Method method : resource.getMethods()) {
            for (final HttpMethod httpMethod : new HttpMethodExtractor().extract(method)) {
                activators.add(new HttpMethodActivator(httpMethod.value(), method, application));
            }
        }
    }

    public Iterable<HttpMethodActivator> activators() {
        return activators;
    }
}