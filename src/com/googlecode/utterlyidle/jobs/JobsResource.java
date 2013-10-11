package com.googlecode.utterlyidle.jobs;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Produces;

import java.util.List;

import static com.googlecode.utterlyidle.jobs.Job.functions.completed;
import static com.googlecode.utterlyidle.jobs.Job.functions.started;
import static com.googlecode.funclate.Model.mutable.model;
import static com.googlecode.totallylazy.Callables.descending;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.RequestBuilder.modify;

import com.googlecode.utterlyidle.schedules.ScheduleResource;

@Path("jobs")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class JobsResource {
    private final Jobs jobs;
    private final Redirector redirector;

    public JobsResource(Jobs jobs, Redirector redirector) {
        this.jobs = jobs;
        this.redirector = redirector;
    }

    @GET
    @Path("list")
    public Model list() {
        List<Model> items = items();
        return model().
                add("anyExists", !items.isEmpty()).
                add("items", items);
    }

    @POST
    @Path("run")
    @Produces(TEXT_PLAIN)
    public Response run(Request request, @PathParam("$") String endOfUrl) throws Exception {
        Request requestToQueue = modify(request).uri(request.uri().path(endOfUrl)).build();
        jobs.run(requestToQueue);
        return ResponseBuilder.response(Status.ACCEPTED.description("Queued HttpJob")).entity("Your HttpJob has been accepted").build();
    }

    @POST
    @Path("deleteAll")
    public Response deleteAll() throws Exception {
        jobs.deleteAll();
        return redirector.seeOther(method(on(JobsResource.class).list()));
    }

    private List<Model> items() {
        return jobs.running().sortBy(descending(started())).map(asRunningModel()).
                join(jobs.completed().sortBy(descending(completed())).map(asCompletedModel())).
                toList();
    }

    private Mapper<RunningJob, Model> asRunningModel() {
        return new Mapper<RunningJob, Model>() {
            @Override
            public Model call(RunningJob runningJob) throws Exception {
                return model().
                        add("status", "running").
                        add("started", runningJob.started().get()).
                        add("completed", "").
                        add("duration", runningJob.duration()).
                        add("request", ScheduleResource.asModel(runningJob.request())).
                        add("response", model());
            }
        };
    }

    private Mapper<CompletedJob, Model> asCompletedModel() {
        return new Mapper<CompletedJob, Model>() {
            @Override
            public Model call(CompletedJob completedJob) throws Exception {
                return model().
                        add("status", "idle").
                        add("started", completedJob.started().get()).
                        add("completed", completedJob.completed().get()).
                        add("duration", completedJob.duration()).
                        add("request", ScheduleResource.asModel(completedJob.request())).
                        add("response", ScheduleResource.asModel(completedJob.response().get()));
            }
        };
    }
}