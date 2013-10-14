package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.Response;

import java.util.Date;

import static com.googlecode.totallylazy.Option.some;

public class RunningJob extends CreatedJob {
    public RunningJob(CreatedJob job, final Date started) {
        super(job.id(), job.request(), job.response(), job.created(), some(started), job.completed());
    }

    public CompletedJob complete(final Response response, final Clock clock) {
        response.toString(); // memorize
        return new CompletedJob(this, response, clock.now());
    }
}