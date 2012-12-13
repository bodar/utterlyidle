package com.googlecode.utterlyidle.services;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.UnaryFunction;
import com.googlecode.totallylazy.collections.ImmutableMap;
import com.googlecode.totallylazy.collections.ListMap;
import com.googlecode.yadic.Container;

import java.util.Iterator;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.collections.ListMap.listMap;

public class Services implements Service, Iterable<Pair<Class<? extends Service>, Services.Status>> {
    private ImmutableMap<Class<? extends Service>, Status> services = ListMap.emptyListMap();
    private final Container container;

    public Services(Container container) {
        this.container = container;
    }

    public Services add(Class<? extends Service> service) {
        services = services.put(service, Status.status(Status.State.stopped, ""));
        return this;
    }

    @Override
    public void start() {
        services = listMap(sequence(services).map(new UnaryFunction<Pair<Class<? extends Service>, Status>>() {
            @Override
            public Pair<Class<? extends Service>, Status> call(Pair<Class<? extends Service>, Status> pair) throws Exception {
                return Pair.<Class<? extends Service>, Status>pair(pair.first(), Services.this.start(pair));
            }
        }));
    }

    private Status start(Pair<Class<? extends Service>, Status> pair) throws Exception {
        try {
            container.get(pair.first()).start();
            return Status.status(Status.State.running, "");
        } catch (Exception e) {
            return Status.status(Status.State.failure, e.getMessage());
        }
    }

    @Override
    public void stop() {
        services = listMap(sequence(services).map(new UnaryFunction<Pair<Class<? extends Service>, Status>>() {
            @Override
            public Pair<Class<? extends Service>, Status> call(Pair<Class<? extends Service>, Status> pair) throws Exception {
                return Pair.<Class<? extends Service>, Status>pair(pair.first(), Services.this.stop(pair));
            }
        }));
    }

    private Status stop(Pair<Class<? extends Service>, Status> pair) throws Exception {
        try {
            container.get(pair.first()).stop();
            return Status.status(Status.State.stopped, "");
        } catch (Exception e) {
            return Status.status(Status.State.failure, e.getMessage());
        }
    }

    @Override
    public Iterator<Pair<Class<? extends Service>, Status>> iterator() {
        return services.iterator();
    }

    public static class Status {
        public enum State {stopped, running, failure}

        public final State state;
        public final String message;

        private Status(State state, String message) {
            this.state = state;
            this.message = message;
        }

        public static Status status(State state, String message) {
            return new Status(state, message);
        }
    }
}