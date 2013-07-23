package com.googlecode.utterlyidle.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static com.googlecode.utterlyidle.jobs.schedule.CountDownScheduler.decorate;

public class CountDownCompleter implements Completer {
    private final Completer delegate;
    private final CountDownLatch latch;

    public CountDownCompleter(Completer delegate, CountDownLatch latch) {
        this.delegate = delegate;
        this.latch = latch;
    }

    @Override
    public void complete(Callable<?> task) {
        delegate.complete(decorate(latch, task));
    }

    @Override
    public void restart() {
        delegate.restart();
    }
}
