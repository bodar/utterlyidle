package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Seconds;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Option.some;

public class RunningJob extends CreatedJob {
    private final Clock clock;

    public RunningJob(CreatedJob job, Clock clock) {
        super(job.id(), job.request(), job.response(), job.created(), some(clock.now()), job.completed());
        this.clock = clock;
    }


    public long duration() {
        return Seconds.between(super.started().get(), clock.now());
    }

    public CompletedJob complete(final Response response) {
        return new CompletedJob(this, response, clock.now());
    }


}