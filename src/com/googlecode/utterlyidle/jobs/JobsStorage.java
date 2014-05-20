package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;

import java.util.UUID;

public interface JobsStorage {
    Option<Job> get(UUID uuid);

    Option<Job> put(Job job);

    void clear();

    Sequence<Job> jobs();
}
