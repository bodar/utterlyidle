package com.googlecode.utterlyidle.jobs.schedule;

import java.util.concurrent.Future;

public class FutureSchedule implements Cancellable {
    private final Future<?> future;

    public FutureSchedule(Future<?> future) {
        this.future = future;
    }

    public void cancel() {
        future.cancel(true);
    }
}
