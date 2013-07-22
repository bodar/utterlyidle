package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.time.Seconds;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Date;


public class CompletedJob {
    public final Request request;
    public final Response response;
    public final Date started;
    public final Date completed;

    public CompletedJob(Request request, Response response, Date started, Date completed) {
        this.request = request;
        this.response = response;
        this.started = started;
        this.completed = completed;
    }

    public long duration() {
        return Seconds.between(started, completed);
    }

    public static Function1<CompletedJob, Date> completed() {
        return new Function1<CompletedJob, Date>() {
            @Override
            public Date call(CompletedJob completedJob) throws Exception {
                return completedJob.completed;
            }
        };
    }
}