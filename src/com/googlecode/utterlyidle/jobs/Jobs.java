package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;

import java.util.UUID;

public interface Jobs {
    Sequence<CompletedJob> completed();

    Sequence<RunningJob> running();

    void deleteAll();

    Job create(Request request);

    Sequence<Job> jobs();
}
