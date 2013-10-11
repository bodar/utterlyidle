package com.googlecode.utterlyidle.jobs;

import java.util.concurrent.Callable;

public class ManualCompleter implements Completer {
    public Callable<?> job;

    @Override
    public void complete(final Callable<?> job) {
        this.job = job;
    }

    @Override
    public void restart() {
    }
}
