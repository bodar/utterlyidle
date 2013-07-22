package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Seconds;
import com.googlecode.utterlyidle.Request;

import java.util.Date;

public class RunningJob {
    public final Request request;
    public final Date started;
    private final Clock clock;

    public RunningJob(Request request, Date started, Clock clock) {
        this.request = request;
        this.started = started;
        this.clock = clock;
    }

    public static Callable1<RunningJob, Date> started() {
        return new Callable1<RunningJob, Date>() {
            @Override
            public Date call(RunningJob runningJob) throws Exception {
                return runningJob.started;
            }
        };
    }

    public long duration() {
        return Seconds.between(started, clock.now());
    }
}