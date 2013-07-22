package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.concurrent.NamedExecutors;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static com.googlecode.totallylazy.Functions.function;

public class CpuBoundedCompleter implements Completer, Closeable {
    private volatile ExecutorService executor;

    @Override
    public void complete(Callable<?> task) {
        executors().execute(function(task));
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
        if (executor == null) executor = NamedExecutors.newCpuThreadPool(CpuBoundedCompleter.class);
    }

    @Override
    public void close() throws IOException {
        stop();
    }
}
