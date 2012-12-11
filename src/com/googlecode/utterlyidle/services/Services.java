package com.googlecode.utterlyidle.services;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class Services implements Iterable<Class<? extends Service>> {
    private final CopyOnWriteArrayList<Class<? extends Service>> services = new CopyOnWriteArrayList<Class<? extends Service>>();

    public Services addService(Class<? extends Service> service) {
        services.add(service);
        return this;
    }

    @Override
    public Iterator<Class<? extends Service>> iterator() {
        return services.iterator();
    }
}