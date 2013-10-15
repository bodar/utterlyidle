package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Date;
import java.util.UUID;

import static com.googlecode.totallylazy.Option.none;

public class CreatedJob implements Job {
    private final UUID id;
    private final Request request;
    private final Option<Response> response;
    private final Date created;
    private final Option<Date> started;
    private final Option<Date> completed;

    protected CreatedJob(final UUID id,
                       final Request request,
                       final Option<Response> response,
                       final Date created,
                       final Option<Date> started,
                       final Option<Date> completed) {
        this.id = id;
        this.request = request;
        this.response = response;
        this.created = created;
        this.completed = completed;
        this.started = started;
    }

    public static CreatedJob createJob(final Request request, final Clock clock) {
        request.toString(); // memorize
        return new CreatedJob(UUID.randomUUID(), request, none(Response.class), clock.now(), none(Date.class), none(Date.class));
    }

    @Override
    public Date created() {
        return created;
    }

    @Override
    public String status() {
        return getClass().getSimpleName().replace("Job", "").toLowerCase();
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Option<Date> completed() {
        return completed;
    }

    @Override
    public Option<Response> response() {
        return response;
    }

    @Override
    public Option<Date> started() {
        return started;
    }

    public RunningJob start(Clock clock) {
        return new RunningJob(this, clock.now());
    }
}
