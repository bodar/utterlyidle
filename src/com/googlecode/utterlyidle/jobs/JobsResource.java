package com.googlecode.utterlyidle.jobs;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.ANY;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Priority;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.schedules.ScheduleResource;

import java.util.UUID;

import static com.googlecode.funclate.Model.persistent.model;
import static com.googlecode.totallylazy.Callables.descending;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.jobs.Job.functions.created;

@Path("jobs")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class JobsResource {
    private final Jobs jobs;
    private final Redirector redirector;
    private final Clock clock;

    public JobsResource(Jobs jobs, Redirector redirector, final Clock clock) {
        this.jobs = jobs;
        this.redirector = redirector;
        this.clock = clock;
    }

    @GET
    @Path("list")
    public Model list() {
        return model().
                add("items", jobs.jobs().
                        sortBy(descending(created)).
                        map(jobModel));
    }

    @ANY
    @Path("create")
    public Response create(Request original, @PathParam("$") String endOfUrl) throws Exception {
        Request request = modify(original).uri(original.uri().path(endOfUrl)).build();
        Job job = jobs.create(request);
        return redirector.seeOther(method(on(JobsResource.class).get(job.id())));
    }

    @GET
    @Priority(Priority.Low)
    @Path("{id}")
    public Response get(@PathParam("id") final UUID id) {
        return jobs.jobs().find(where(Job.functions.id, is(id))).
                map(jobResponse).
                get();
    }

    @POST
    @Path("deleteAll")
    public Response deleteAll() throws Exception {
        jobs.deleteAll();
        return redirector.seeOther(method(on(JobsResource.class).list()));
    }

    private Mapper<Job, Model> jobModel = new Mapper<Job, Model>() {
        @Override
        public Model call(Job job) throws Exception {
            return jobModel(job);
        }
    };

    private Mapper<Job,Response> jobResponse = new Mapper<Job, Response>() {
        @Override
        public Response call(final Job job) throws Exception {
            return ResponseBuilder.response(job.completed().isEmpty() ? Status.ACCEPTED : Status.OK).
                    entity(jobModel(job)).
                    build();
        }
    };

    private Model jobModel(final Job job) {
        return model().
                add("status", job.status()).
                add("created", job.created()).
                addOptionally("started", job.started()).
                addOptionally("completed", job.completed()).
                addOptionally("duration", Job.methods.duration(job, clock)).
                add("request", ScheduleResource.asModel(job.request())).
                addOptionally("response", job.response().map(ScheduleResource.asModel));
    }
}