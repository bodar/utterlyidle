package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;

public interface Jobs {
    Sequence<CompletedJob> completed();

    void run(Request request);

    Sequence<RunningJob> running();

    void deleteAll();
}