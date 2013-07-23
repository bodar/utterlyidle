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
import static com.googlecode.utterlyidle.jobs.schedule.Schedule.asScheduleId;

@Path("schedules")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class BatchScheduleResource {
    private InvocationHandler invocationHandler;
    private Schedules schedules;
    private HttpScheduler scheduler;
    private Redirector redirector;

    public BatchScheduleResource(final InvocationHandler invocationHandler, final Schedules schedules, final HttpScheduler scheduler, final Redirector redirector) {
        this.invocationHandler = invocationHandler;
        this.schedules = schedules;
        this.scheduler = scheduler;
        this.redirector = redirector;
    }

    @POST
    @Path("start")
    public Response start() {
        scheduler.start();
        return redirector.seeOther(method(on(ScheduleResource.class).list()));
    }

    @POST
    @Path("stop")
    public Response stop() {
        scheduler.stop();
        return redirector.seeOther(method(on(ScheduleResource.class).list()));
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
                return invocationHandler.handle(method(on(ScheduleResource.class).delete(uuid)));
            }
        };
    }

    private Sequence<UUID> ids() {
        return schedules.schedules().map(asScheduleId());
    }
}