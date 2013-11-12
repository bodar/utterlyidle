package com.googlecode.utterlyidle.jobs;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Responses;
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
    private final JobsStorage storage;
    private final Redirector redirector;
    private final Clock clock;

    public JobsResource(Jobs jobs, JobsStorage storage, Redirector redirector, final Clock clock) {
        this.jobs = jobs;
        this.storage = storage;
        this.redirector = redirector;
        this.clock = clock;
    }

    @GET
    @Path("list")
    public Model list() {
        return model().
                add("items", storage.jobs().
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
        return jobResponse(id, jobResponse.optional());
    }

    @GET
    @Path("{id}/result")
    public Response result(@PathParam("id") final UUID id) {
        return jobResponse(id, Job.functions.response);
    }

    private Response jobResponse(final UUID id, final Callable1<? super Job, ? extends Option<Response>> mapper) {
        return storage.get(id).
                flatMap(mapper).
                getOrElse(Responses.response(Status.NOT_FOUND));
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
            return response(job);
        }
    };

    private Response response(final Job job) {
        return setResultLocation(job, ResponseBuilder.response(status(job)).
                entity(jobModel(job)).
                build());
    }

    private Response setResultLocation(final Job job, final Response response) {
        if(response.status().equals(Status.OK)) {
            return ResponseBuilder.modify(response).
                    header(HttpHeaders.CONTENT_LOCATION, resultUri(job)).build();
        }
        return response;
    }

    private Uri resultUri(final Job job) {
        return redirector.uriOf(method(on(JobsResource.class).result(job.id())));
    }

    public static Status status(final Job job) {
        if(job.completed().isDefined()) return Status.OK;
        if(job.started().isDefined()) return Status.ACCEPTED;
        return Status.CREATED;
    }

    private Model jobModel(final Job job) {
        return model().
                add("id", job.id()).
                add("status", job.status()).
                add("created", job.created()).
                add("result", resultUri(job)).
                addOptionally("started", job.started()).
                addOptionally("completed", job.completed()).
                addOptionally("duration", Job.methods.duration(job, clock)).
                add("request", ScheduleResource.asModel(job.request())).
                addOptionally("response", job.response().map(ScheduleResource.asModel));
    }
}