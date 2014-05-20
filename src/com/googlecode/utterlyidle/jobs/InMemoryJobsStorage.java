package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Value;

import java.util.Map;
import java.util.UUID;

import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Option.option;
import static java.util.Collections.synchronizedMap;

public class InMemoryJobsStorage implements JobsStorage, Value<Map<UUID, Job>> {
    private final Map<UUID, Job> value;

    public InMemoryJobsStorage(final JobsHistoryCapacity capacity) {
        this(synchronizedMap(Maps.<UUID, Job>fifoMap(capacity.value())));
    }

    private InMemoryJobsStorage(final Map<UUID, Job> value) {
        this.value = value;
    }

    @Override
    public Map<UUID, Job> value() {
        return value;
    }

    @Override
    public Option<Job> get(UUID uuid) {
        return option(value.get(uuid));
    }

    @Override
    public Option<Job> put(final Job job) {
        return option(value.put(job.id(), job));
    }

    @Override
    public void clear() {
        value.clear();
    }

    @Override
    public Sequence<Job> jobs() {
        return Maps.pairs(value).map(second(Job.class));
    }
}
