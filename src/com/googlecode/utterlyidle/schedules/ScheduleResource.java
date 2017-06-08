package com.googlecode.utterlyidle.schedules;

import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.ANY;
import com.googlecode.utterlyidle.annotations.FormParam;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.annotations.QueryParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.HttpMessageParser.parseRequest;
import static com.googlecode.utterlyidle.HttpMessageParser.parseResponse;
import static com.googlecode.utterlyidle.Request.Builder.uri;

@Path(ScheduleResource.PATH)
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class ScheduleResource {
    public static final String PATH = "schedules";

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
        Request scheduledRequest = modify(request, uri(request.uri().path(endOfUrl)));

        scheduler.schedule(Schedule.schedule(id).interval(intervalInSeconds).request(scheduledRequest.toString()));

        return redirectToList();
    }

    @ANY
    @Path("schedule/{id}/{start}/{interval}")
    public Response schedule(@PathParam("id") UUID id, @PathParam("start") String start, @PathParam("interval") Long intervalInSeconds, @PathParam("$") String endOfUrl) throws Exception {
        Request scheduledRequest = modify(request, uri(request.uri().path(endOfUrl)));

        scheduler.schedule(Schedule.schedule(id).start(start).interval(intervalInSeconds).request(scheduledRequest.toString()));

        return redirectToList();
    }

    @ANY
    @Path("schedule")
    public Response scheduleWithQueryParams(@QueryParam("id") UUID id, @QueryParam("start") Option<String> start, @QueryParam("interval") Long intervalInSeconds, @QueryParam("uri") String uri) {
        Request scheduledRequest = modify(request, uri(Uri.uri(uri)));

        Schedule schedule = Schedule.schedule(id).interval(intervalInSeconds).request(scheduledRequest.toString());
        if (start.isDefined()) {
            schedule = schedule.start(start.get());
        }
        scheduler.schedule(schedule);

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
    public Map<String,Object> edit(@QueryParam("id") UUID id) {
        Record schedule = scheduler.schedule(id).get();
        return Maps.map("id", id.toString(), "seconds", schedule.get(SchedulesDefinition.interval), "start", schedule.get(SchedulesDefinition.start));
    }

    @POST
    @Path("delete")
    public Response delete(@FormParam("id") UUID id) {
        scheduler.remove(id);
        return redirectToList();
    }

    @GET
    @Path("list")
    public Map<String,Object> list() {
        List<Map<String,Object>> models = schedulesModel(scheduler.schedules());
        return Maps.map("schedules", models,
                "schedulerIsRunning", scheduler.isRunning());
    }

    private Response redirectToList() {
        return redirector.seeOther(method(on(getClass()).list()));
    }

    public static List<Map<String,Object>> schedulesModel(Sequence<Record> schedules) {
        return schedules.map(record -> (Map<String, Object>) new HashMap<String,Object>() {{
            put("id", record.get(SchedulesDefinition.scheduleId));
            put("status", Boolean.TRUE.equals(record.get(SchedulesDefinition.running)) ? "running" : "idle");
            put("start", record.get(SchedulesDefinition.start));
            put("seconds", record.get(SchedulesDefinition.interval));
            put("request", addRequest(record));
            put("response", addResponse(record));
            put("started", record.get(SchedulesDefinition.started));
            put("completed", record.get(SchedulesDefinition.completed));
            put("duration", record.get(SchedulesDefinition.duration));
        }}).toList();
    }

    public static Map<String,Object> addRequest(Record record) {
        String requestMessage = record.get(SchedulesDefinition.request);
        if (requestMessage == null) {
            return null;
        }
        Request request = parseRequest(requestMessage);
        return asModel(request);
    }

    public static Map<String,Object> asModel(Request request) {
        return new HashMap<String,Object>(){{
                put("raw", request.toString());
                put("method", request.method());
                put("uri", request.uri());
                put("entity", request.entity().toString());
            }};
    }

    public static Map<String,Object> addResponse(Record record) {
        String responseMessage = record.get(SchedulesDefinition.response);
        if (responseMessage == null) {
            return null;
        }
        Response response = parseResponse(responseMessage);
        return asModel(response);
    }

    public static Map<String,Object> asModel(Response response) {
        Status status = response.status();
        return new HashMap<String,Object>(){ {
                put("raw", response.toString());
                put("code", status.code());
                put("status", status.description());
                put("entity", response.entity().toString());
                put("isOk", status.isSuccessful() || status.isRedirect());
            }};
        }

}