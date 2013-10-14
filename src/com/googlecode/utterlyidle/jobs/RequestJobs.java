package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;

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
    private final List<CreatedJob> created = new CopyOnWriteArrayList<CreatedJob>();
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
    public Job create(final Request request) {
        CreatedJob job = CreatedJob.createJob(request, clock.now());
        created.add(job);
        completer.complete(handle(job));
        return job;
    }

    @Override
    public Sequence<Job> jobs() {
        return Sequences.<Job>join(created, running, completed);
    }

    @Override
    public void deleteAll() {
        completer.restart();
        created.clear();
        running.clear();
        completed.clear();
    }

    private void complete(CreatedJob job) {
        RunningJob runningJob = job.start(clock);
        created.remove(job);
        running.add(runningJob);
        Response response = responseFor(runningJob.request());
        running.remove(runningJob);
        completed.add(runningJob.complete(response, clock));
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
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                complete(job);
                return VOID;
            }
        };
    }

}
