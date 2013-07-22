package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.funclate.Model;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.annotations.ANY;
import com.googlecode.utterlyidle.annotations.FormParam;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.annotations.QueryParam;

import java.util.List;
import java.util.UUID;

import static com.googlecode.funclate.Model.mutable.model;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.HttpMessageParser.parseRequest;
import static com.googlecode.utterlyidle.HttpMessageParser.parseResponse;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.jobs.schedule.Job.COMPLETED;
import static com.googlecode.utterlyidle.jobs.schedule.Job.DURATION;
import static com.googlecode.utterlyidle.jobs.schedule.Job.INTERVAL;
import static com.googlecode.utterlyidle.jobs.schedule.Job.JOB_ID;
import static com.googlecode.utterlyidle.jobs.schedule.Job.REQUEST;
import static com.googlecode.utterlyidle.jobs.schedule.Job.RESPONSE;
import static com.googlecode.utterlyidle.jobs.schedule.Job.RUNNING;
import static com.googlecode.utterlyidle.jobs.schedule.Job.START;
import static com.googlecode.utterlyidle.jobs.schedule.Job.STARTED;
import static com.googlecode.utterlyidle.jobs.schedule.Job.job;

@Path("jobs")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class JobsResource {
    private final HttpScheduler scheduler;
    private final Request request;
    private final Redirector redirector;

    public JobsResource(HttpScheduler scheduler, Request request, Redirector redirector, InternalRequestMarker internalRequestMarker) {
        this.scheduler = scheduler;
        this.request = internalRequestMarker.markAsInternal(request);
        this.redirector = redirector;
    }

    @ANY
    @Path("schedule/{id}/{interval}")
    public Response schedule(@PathParam("id") UUID id, @PathParam("interval") Long intervalInSeconds, @PathParam("$") String endOfUrl) throws Exception {
        Request scheduledRequest = modify(request).uri(request.uri().path(endOfUrl)).build();

        scheduler.schedule(job(id).interval(intervalInSeconds).request(scheduledRequest.toString()));

        return redirectToList();
    }

    @ANY
    @Path("schedule/{id}/{start}/{interval}")
    public Response schedule(@PathParam("id") UUID id, @PathParam("start") String start, @PathParam("interval") Long intervalInSeconds, @PathParam("$") String endOfUrl) throws Exception {
        Request scheduledRequest = modify(request).uri(request.uri().path(endOfUrl)).build();

        scheduler.schedule(job(id).start(start).interval(intervalInSeconds).request(scheduledRequest.toString()));

        return redirectToList();
    }

    @POST
    @Path("reschedule")
    public Response reschedule(@FormParam("id") UUID id, @FormParam("seconds") Long seconds, @FormParam("start") String start) throws Exception {
        scheduler.schedule(job(id).interval(seconds).start(start));
        return redirectToList();
    }

    @GET
    @Path("edit")
    public Model edit(@QueryParam("id") UUID id) {
        Record job = scheduler.job(id).get();
        return model().add("id", id.toString()).add("seconds", job.get(INTERVAL)).add("start", job.get(START));
    }

    @POST
    @Path("delete")
    public Response delete(@FormParam("id") UUID id) {
        scheduler.remove(id);
        return redirectToList();
    }

    @GET
    @Path("list")
    public Model list() {
        List<Model> models = jobsModel(scheduler.jobs());
        return model().add("jobs", models).add("anyExists", !models.isEmpty());
    }

    private Response redirectToList() {
        return redirector.seeOther(method(on(getClass()).list()));
    }

    public static List<Model> jobsModel(Sequence<Record> jobs) {
        return jobs.map(toModel()).toList();
    }

    public static Callable1<? super Record, Model> toModel() {
        return new Callable1<Record, Model>() {
            public Model call(Record record) throws Exception {
                return model().
                        add("id", record.get(JOB_ID)).
                        add("status", Boolean.TRUE.equals(record.get(RUNNING)) ? "running" : "idle").
                        add("start", record.get(START)).
                        add("seconds", record.get(INTERVAL)).
                        add("request", addRequest(record)).
                        add("response", addResponse(record)).
                        add("started", record.get(STARTED)).
                        add("completed", record.get(COMPLETED)).
                        add("duration", record.get(DURATION));
            }
        };
    }

    public static Model addRequest(Record record) {
        String requestMessage = record.get(REQUEST);
        if (requestMessage == null) {
            return null;
        }
        Request request = parseRequest(requestMessage);
        return asModel(request);
    }

    public static Model asModel(Request request) {
        return model().
                add("raw", request.toString()).
                add("method", request.method()).
                add("uri", request.uri()).
                add("entity", request.entity().toString());
    }

    public static Model addResponse(Record record) {
        String responseMessage = record.get(RESPONSE);
        if (responseMessage == null) {
            return null;
        }
        Response response = parseResponse(responseMessage);
        return asModel(response);
    }

    public static Model asModel(Response response) {
        return model().
                add("raw", response.toString()).
                add("code", response.status().code()).
                add("status", response.status().description()).
                add("entity", response.entity().toString());
    }
}