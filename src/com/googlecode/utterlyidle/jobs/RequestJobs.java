package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Runnables.VOID;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;

public class RequestJobs implements Jobs {
    private final Application application;
    private final Clock clock;
    private final Completer completer;
    private final JobsStorage storage;

    public RequestJobs(Application application, Clock clock, Completer completer, JobsStorage storage) {
        this.application = application;
        this.clock = clock;
        this.completer = completer;
        this.storage = storage;
    }

    @Override
    public Job create(final Request request) {
        CreatedJob job = CreatedJob.createJob(request, clock);
        storage.put(job);
        completer.complete(handle(job));
        return job;
    }

    @Override
    public void deleteAll() {
        completer.restart();
        storage.clear();
    }

    private void complete(CreatedJob job) {
        RunningJob runningJob = job.start(clock);
        storage.put(runningJob);
        Response response = responseFor(runningJob.request());
        storage.put(runningJob.complete(response, clock));
    }

    private Response responseFor(Request request) {
        try {
            return application.handle(request);
        } catch (Exception e) {
            return response(INTERNAL_SERVER_ERROR).
                    contentType(TEXT_PLAIN).
                    entity(ExceptionRenderer.toString(e)).
                    build();
        }
    }

    private Callable<Void> handle(final CreatedJob job) {
        return () -> {
            complete(job);
            return VOID;
        };
    }

}
