package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Maps;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Callable;

public class JobsStorageActivator implements Callable<JobsStorage> {
    private final JobsHistoryCapacity capacity;

    public JobsStorageActivator(final JobsHistoryCapacity capacity) {
        this.capacity = capacity;
    }

    @Override
    public JobsStorage call() throws Exception {
        return new JobsStorage(Collections.synchronizedMap(Maps.<UUID, Job>fifoMap(capacity.value())));
    }
}
