package com.googlecode.utterlyidle.jobs;

import com.googlecode.utterlyidle.Request;

public interface Jobs {
    void deleteAll();

    Job create(Request request);
}
