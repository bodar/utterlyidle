package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.handlers.InvocationHandler;

import java.util.UUID;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.jobs.schedule.Job.asJobId;

@Path("jobs")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class BatchJobsResource {
    private InvocationHandler invocationHandler;
    private Jobs jobs;
    private HttpScheduler scheduler;
    private Redirector redirector;

    public BatchJobsResource(final InvocationHandler invocationHandler, final Jobs jobs, final HttpScheduler scheduler, final Redirector redirector) {
        this.invocationHandler = invocationHandler;
        this.jobs = jobs;
        this.scheduler = scheduler;
        this.redirector = redirector;
    }

    @POST
    @Path("start")
    public Response start() {
        scheduler.start();
        return redirector.seeOther(method(on(JobsResource.class).list()));
    }

    @POST
    @Path("stop")
    public Response stop() {
        scheduler.stop();
        return redirector.seeOther(method(on(JobsResource.class).list()));
    }

    @POST
    @Path("deleteAll")
    public Response deleteAll() throws Exception {
        scheduler.stop();
        return ids().map(delete()).last();
    }

    public Callable1<UUID, Response> delete() {
        return new Callable1<UUID, Response>() {
            public Response call(UUID uuid) throws Exception {
                return invocationHandler.handle(method(on(JobsResource.class).delete(uuid)));
            }
        };
    }

    private Sequence<UUID> ids() {
        return jobs.jobs().map(asJobId());
    }
}