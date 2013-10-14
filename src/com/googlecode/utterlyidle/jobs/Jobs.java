package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;

public interface Jobs {
    Sequence<Job> jobs();

    void deleteAll();

    Job create(Request request);

}
