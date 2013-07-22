package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;

public interface Queues {
    Sequence<CompletedJob> completed();

    void queue(Request request);

    Sequence<RunningJob> running();

    void deleteAll();
}
