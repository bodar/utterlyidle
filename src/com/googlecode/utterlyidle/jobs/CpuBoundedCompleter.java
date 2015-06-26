package com.googlecode.utterlyidle.jobs;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static com.googlecode.totallylazy.functions.Functions.function;
import static com.googlecode.totallylazy.concurrent.NamedExecutors.newCpuThreadPool;

public class CpuBoundedCompleter implements Completer, Closeable {
    private volatile ExecutorService executor;

    @Override
    public void complete(Callable<?> job) {
        executors().execute(function(job));
    }

    private synchronized Executor executors() {
        if (executor == null) start();
        return executor;
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    public synchronized void stop() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    public synchronized void start() {
        if (executor == null) executor = newCpuThreadPool(CpuBoundedCompleter.class);
    }

    @Override
    public void close() throws IOException {
        stop();
    }
}