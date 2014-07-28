package com.googlecode.utterlyidle.services;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Debug;
import com.googlecode.totallylazy.Exceptions;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.UnaryFunction;
import com.googlecode.totallylazy.collections.PersistentMap;
import com.googlecode.totallylazy.collections.ListMap;
import com.googlecode.utterlyidle.Application;
import com.googlecode.yadic.Container;

import java.util.Iterator;

import static com.googlecode.totallylazy.Debug.inDebug;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.collections.ListMap.listMap;

public class Services implements Service, Iterable<Pair<Class<? extends Service>, Services.Status>> {
    private final Application application;
    private PersistentMap<Class<? extends Service>, Status> services = ListMap.emptyListMap();

    public Services(Application application) {
        this.application = application;
    }

    public Services add(Class<? extends Service> service) {
        services = services.insert(service, Status.status(Status.State.stopped, ""));
        return this;
    }

    public Services addAndRegister(Class<? extends Service> service) {
        application.applicationScope().add(service);
        return add(service);
    }

    @Override
    public void start() {
        services = listMap(sequence(services).map(new UnaryFunction<Pair<Class<? extends Service>, Status>>() {
            @Override
            public Pair<Class<? extends Service>, Status> call(Pair<Class<? extends Service>, Status> services) throws Exception {
                return Pair.<Class<? extends Service>, Status>pair(services.first(), Services.this.start(services));
            }
        }));
    }

    private Status start(final Pair<Class<? extends Service>, Status> services) throws Exception {
        try {
            application.usingRequestScope(new Block<Container>() {
                @Override
                protected void execute(Container container) throws Exception {
                    container.get(services.first()).start();
                }
            });
            return Status.status(Status.State.running, "");
        } catch (Exception e) {
            return Status.status(Status.State.failure, Exceptions.asString(e));
        }
    }

    @Override
    public void stop() {
        services = listMap(sequence(services).map(new UnaryFunction<Pair<Class<? extends Service>, Status>>() {
            @Override
            public Pair<Class<? extends Service>, Status> call(Pair<Class<? extends Service>, Status> services) throws Exception {
                return Pair.<Class<? extends Service>, Status>pair(services.first(), Services.this.stop(services));
            }
        }));
    }

    private Status stop(final Pair<Class<? extends Service>, Status> services) throws Exception {
        try {
            application.usingRequestScope(new Block<Container>() {
                @Override
                protected void execute(Container container) throws Exception {
                    container.get(services.first()).stop();
                }
            });
            return Status.status(Status.State.stopped, "");
        } catch (Exception e) {
            return Status.status(Status.State.failure, Exceptions.asString(e));
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
            if(state == State.failure && inDebug()) System.out.println(message);
            return new Status(state, message);
        }
    }
}