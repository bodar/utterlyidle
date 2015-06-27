package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Exceptions;
import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.http.Uri;
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
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.schedules.ScheduleResource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.googlecode.totallylazy.functions.Callables.descending;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.jobs.Job.functions.created;

@Path(JobsResource.JOBS)
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class JobsResource {
    public static final String JOBS = "jobs";
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
    public Map<String,Object> list() {
        return Maps.map("items", storage.jobs().
                        sortBy(descending(created)).
                        map(this::jobModel));
    }

    @ANY
    @Path("create")
    public Response create(Request original, @PathParam("$") String endOfUrl) throws Exception {
        Request request = modify(original).uri(original.uri().path(endOfUrl)).build();
        Job job = jobs.create(request);
        return redirector.seeOther(method(on(JobsResource.class).get(job.id())));
    }

    @GET
    @Path("{id:(?!list$).+}")
    public Response get(@PathParam("id") final UUID id) {
        return jobResponse(id, Exceptions.optional(this::response));
    }

    @GET
    @Path("{id}/result")
    public Response result(@PathParam("id") final UUID id) {
        return jobResponse(id, Job.functions.response);
    }

    private Response jobResponse(final UUID id, final Function1<? super Job, ? extends Option<Response>> mapper) {
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

    private Response response(final Job job) {
        return setResultLocation(job, ResponseBuilder.response(status(job)).
                entity(jobModel(job)).
                build());
    }

    private Response setResultLocation(final Job job, final Response response) {
        if (response.status().equals(Status.OK)) {
            return ResponseBuilder.modify(response).
                    header(HttpHeaders.CONTENT_LOCATION, resultUri(job)).build();
        }
        return response;
    }

    private Uri resultUri(final Job job) {
        return redirector.uriOf(method(on(JobsResource.class).result(job.id())));
    }

    public static Status status(final Job job) {
        if (job.completed().isDefined()) return Status.OK;
        if (job.started().isDefined()) return Status.ACCEPTED;
        return Status.CREATED;
    }

    private Map<String,Object> jobModel(final Job job) {
        return new HashMap<String, Object>(){ {
                put("id", job.id());
                put("status", job.status());
                put("created", job.created());
                put("result", resultUri(job));
                putOptionally(this, "started", job.started());
                putOptionally(this, "completed", job.completed());
                putOptionally(this, "duration", Job.methods.duration(job, clock));
                put("request", ScheduleResource.asModel(job.request()));
                putOptionally(this, "response", job.response().map(ScheduleResource::asModel));
            }};
    }

    public static <K> void putOptionally(final Map<K, Object> map, final K key, final Option<?> optional) {
        for (Object value : optional) map.put(key, value);
    }
}