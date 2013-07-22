package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.totallylazy.Option;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;

public interface Scheduler {
    void schedule(UUID id, Callable<?> command, Option<Date> start, long numberOfSeconds);

    void cancel(UUID id);
}