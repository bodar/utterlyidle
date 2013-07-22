package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.concurrent.NamedExecutors;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.services.Service;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

import static com.googlecode.totallylazy.Functions.function;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.time.Seconds.between;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FixedScheduler implements Scheduler, Closeable, Service {
    private final Map<UUID, Cancellable> schedule = new HashMap<UUID, Cancellable>();
    private volatile ScheduledExecutorService service;
    private final Clock clock;

    public FixedScheduler(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void schedule(UUID id, Callable<?> command, Option<Date> start, long numberOfSeconds) {
        cancel(id);
        Date now = clock.now();
        FutureSchedule futureSchedule = new FutureSchedule(service().scheduleAtFixedRate(function(command), between(now, start.getOrElse(now)), numberOfSeconds, SECONDS));
        schedule.put(id, futureSchedule);
    }

    synchronized private ScheduledExecutorService service() {
        return service == null ? this.service = NamedExecutors.newScheduledThreadPool(5, getClass().getName()) : service;
    }

    public void cancel(UUID id) {
        Cancellable schedule = this.schedule.remove(id);
        if (schedule != null) {
            schedule.cancel();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            while (!schedule.keySet().isEmpty()) {
                cancel(sequence(schedule.keySet()).first());
            }
        } catch (Exception ignore) {
        }
        if(service !=  null) service.shutdownNow();
        service = null;
    }

    @Override
    public void start() throws Exception {
        service();
    }

    @Override
    synchronized public void stop() throws Exception {
        close();
    }
}