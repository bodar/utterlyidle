package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Value;

import java.util.Map;
import java.util.UUID;

import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Option.option;

public class JobsStorage implements Value<Map<UUID, Job>> {
    private final Map<UUID, Job> value;

    public JobsStorage(final Map<UUID, Job> value) {
        this.value = value;
    }

    @Override
    public Map<UUID, Job> value() {
        return value;
    }

    public Option<Job> get(UUID uuid) {
        return option(value.get(uuid));
    }

    public Option<Job> put(final Job job) {
        return option(value.put(job.id(), job));
    }

    public void clear() {
        value.clear();
    }

    public Sequence<Job> jobs() {
        return Maps.pairs(value).map(second(Job.class));
    }
}
