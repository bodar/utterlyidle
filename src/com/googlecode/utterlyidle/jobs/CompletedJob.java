package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.time.Seconds;
import com.googlecode.utterlyidle.Response;

import java.util.Date;

import static com.googlecode.totallylazy.Option.some;


public class CompletedJob extends CreatedJob {
    public CompletedJob(RunningJob job, Response response, Date completed) {
        super(job.id(), job.request(), some(response), job.created(), job.started(), some(completed));
    }

    public long duration() {
        return Seconds.between(started().get(), completed().get());
    }
}