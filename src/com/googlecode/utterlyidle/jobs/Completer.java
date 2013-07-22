package com.googlecode.utterlyidle.jobs;

import java.util.concurrent.Callable;

public interface Completer {
    void complete(Callable<?> task);

    void restart();
}
