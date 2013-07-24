package com.googlecode.utterlyidle.schedules;

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

@Path("schedules")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class ScheduleResource {
    private final HttpScheduler scheduler;
    private final Request request;
    private final Redirector redirector;

    public ScheduleResource(HttpScheduler scheduler, Request request, Redirector redirector, InternalRequestMarker internalRequestMarker) {
        this.scheduler = scheduler;
        this.request = internalRequestMarker.markAsInternal(request);
        this.redirector = redirector;
    }

    @ANY
    @Path("schedule/{id}/{interval}")
    public Response schedule(@PathParam("id") UUID id, @PathParam("interval") Long intervalInSeconds, @PathParam("$") String endOfUrl) throws Exception {
        Request scheduledRequest = modify(request).uri(request.uri().path(endOfUrl)).build();

        scheduler.schedule(Schedule.schedule(id).interval(intervalInSeconds).request(scheduledRequest.toString()));

        return redirectToList();
    }

    @ANY
    @Path("schedule/{id}/{start}/{interval}")
    public Response schedule(@PathParam("id") UUID id, @PathParam("start") String start, @PathParam("interval") Long intervalInSeconds, @PathParam("$") String endOfUrl) throws Exception {
        Request scheduledRequest = modify(request).uri(request.uri().path(endOfUrl)).build();

        scheduler.schedule(Schedule.schedule(id).start(start).interval(intervalInSeconds).request(scheduledRequest.toString()));

        return redirectToList();
    }

    @POST
    @Path("reschedule")
    public Response reschedule(@FormParam("id") UUID id, @FormParam("seconds") Long seconds, @FormParam("start") String start) throws Exception {
        scheduler.schedule(Schedule.schedule(id).interval(seconds).start(start));
        return redirectToList();
    }

    @GET
    @Path("edit")
    public Model edit(@QueryParam("id") UUID id) {
        Record schedule = scheduler.schedule(id).get();
        return model().add("id", id.toString()).add("seconds", schedule.get(SchedulesDefinition.interval)).add("start", schedule.get(SchedulesDefinition.start));
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
        List<Model> models = schedulesModel(scheduler.schedules());
        return model().add("schedules", models).add("anyExists", !models.isEmpty());
    }

    private Response redirectToList() {
        return redirector.seeOther(method(on(getClass()).list()));
    }

    public static List<Model> schedulesModel(Sequence<Record> schedules) {
        return schedules.map(toModel()).toList();
    }

    public static Callable1<? super Record, Model> toModel() {
        return new Callable1<Record, Model>() {
            public Model call(Record record) throws Exception {
                return model().
                        add("id", record.get(SchedulesDefinition.scheduleId)).
                        add("status", Boolean.TRUE.equals(record.get(SchedulesDefinition.running)) ? "running" : "idle").
                        add("start", record.get(SchedulesDefinition.start)).
                        add("seconds", record.get(SchedulesDefinition.interval)).
                        add("request", addRequest(record)).
                        add("response", addResponse(record)).
                        add("started", record.get(SchedulesDefinition.started)).
                        add("completed", record.get(SchedulesDefinition.completed)).
                        add("duration", record.get(SchedulesDefinition.duration));
            }
        };
    }

    public static Model addRequest(Record record) {
        String requestMessage = record.get(SchedulesDefinition.request);
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
        String responseMessage = record.get(SchedulesDefinition.response);
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