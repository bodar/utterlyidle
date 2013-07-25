package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static com.googlecode.totallylazy.Runnables.VOID;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;

public class RequestJobs implements Jobs {
    private final List<RunningJob> running = new CopyOnWriteArrayList<RunningJob>();
    private final LinkedBlockingQueue<CompletedJob> completed;
    private final Application application;
    private final Clock clock;
    private final Completer completer;

    public RequestJobs(Application application, Clock clock, Completer completer, JobsHistoryCapacity capacity) {
        this.completed = new CappedLinkedBlockingQueue<CompletedJob>(capacity.value());
        this.application = application;
        this.clock = clock;
        this.completer = completer;
    }

    @Override
    public Sequence<CompletedJob> completed() {
        return sequence(completed);
    }

    @Override
    public void run(final Request request) {
        completer.complete(handle(request));
    }

    @Override
    public Sequence<RunningJob> running() {
        return sequence(running);
    }

    @Override
    public void deleteAll() {
        completer.restart();
        running.clear();
        completed.clear();
    }

    private void complete(Request request) {
        Date started = clock.now();
        RunningJob runningJob = new RunningJob(request, started, clock);
        running.add(runningJob);
        Response response = responseFor(request);
        running.remove(runningJob);
        Date completedDate = clock.now();
        completed.add(new CompletedJob(request, response, started, completedDate));
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

    private Callable<Void> handle(final Request request) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                complete(request);
                return VOID;
            }
        };
    }

}
