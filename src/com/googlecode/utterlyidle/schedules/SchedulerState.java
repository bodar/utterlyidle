package com.googlecode.utterlyidle.schedules;

import java.util.concurrent.atomic.AtomicBoolean;

public class SchedulerState {
    private final AtomicBoolean running = new AtomicBoolean(false);

    public boolean isRunning() {
        return running.get();
    }

    public SchedulerState setRunning(boolean isRunning) {
        running.getAndSet(isRunning);
        return this;
    }
}
