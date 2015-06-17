package com.googlecode.utterlyidle.schedules;

import com.googlecode.totallylazy.Option;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class CountDownScheduler implements Scheduler {
    private final Scheduler scheduler;
    private final CountDownLatch latch;

    public CountDownScheduler(Scheduler scheduler, CountDownLatch latch) {
        this.scheduler = scheduler;
        this.latch = latch;
    }

    @Override
    public void schedule(UUID id, Callable<?> command, Option<Date> start, long numberOfSeconds) {
        scheduler.schedule(id, decorate(latch, command), start, numberOfSeconds);
    }

    public void cancel(UUID id) {
        scheduler.cancel(id);
    }

    public static <T> Callable<T> decorate(final CountDownLatch latch, final Callable<T> command) {
        return () -> {
            T result = command.call();
            latch.countDown();
            return result;
        };
    }
}
