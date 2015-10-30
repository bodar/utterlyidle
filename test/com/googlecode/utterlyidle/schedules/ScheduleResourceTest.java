package com.googlecode.utterlyidle.schedules;

import com.googlecode.lazyrecords.memory.MemoryRecords;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.handlers.ApplicationId;
import com.googlecode.utterlyidle.jobs.SpyExceptionLogger;
import com.googlecode.utterlyidle.jobs.UtterlyIdleRecords;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.Request.Builder.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ScheduleResourceTest {

    private static final UUID SCHEDULE_ID = UUID.randomUUID();

    private StubHttpScheduler stubHttpScheduler;
    private Request requestToSchedule;
    private InternalRequestMarker internalRequestMarker;
    private ScheduleResource scheduleResource;

    @Before
    public void setUp() {
        stubHttpScheduler = new StubHttpScheduler();
        requestToSchedule = post("/foo/bar", query("queryParam", "value"), header("myHeader", "myHeaderValue"), entity("entityValue"));
        internalRequestMarker = new InternalRequestMarker(ApplicationId.applicationId());
        scheduleResource = new ScheduleResource(stubHttpScheduler, requestToSchedule, stubRedirector(), internalRequestMarker);
    }

    @Test
    public void shouldScheduleWithQueryParameters() throws Exception {
        scheduleResource.scheduleWithQueryParams(SCHEDULE_ID, none(String.class), 60L, requestToSchedule.uri().toString());
        final Schedule expected = Schedule.schedule(SCHEDULE_ID).interval(60L).request(internalRequestMarker.markAsInternal(requestToSchedule).toString());
        assertThat(stubHttpScheduler.lastSchedule, is(expected));
    }

    @Test
    public void shouldScheduleWithSpecifiedStartTimeWithQueryParameters() throws Exception {
        scheduleResource.scheduleWithQueryParams(SCHEDULE_ID, some("0600"), 60L, requestToSchedule.uri().toString());
        final Schedule expected = Schedule.schedule(SCHEDULE_ID).interval(60L).request(internalRequestMarker.markAsInternal(requestToSchedule).toString()).start("0600");
        assertThat(stubHttpScheduler.lastSchedule, is(expected));
    }

    @Test
    public void scheduleResourceShouldNotThrowExceptionsInDebugMode() throws Exception {
        final SpyExceptionLogger logger = new SpyExceptionLogger();
        final ApplicationBuilder application = application().add((RequestScopedModule) container -> {
            container.remove(ExceptionLogger.class);
            return container.
                    addInstance(ExceptionLogger.class, logger).
                    addInstance(UtterlyIdleRecords.class, new UtterlyIdleRecords(new MemoryRecords()));
        }).add(new ScheduleModule());
        assertThat(application.handle(get("schedules/schedule", query("id", "93f78f30-d7db-11e1-9b23-0800200c9a66"), query("interval", "60"), query("uri", "/jobs/create/crawler/crawl"), entity("id=93f78f30-d7db-11e1-9b23-0800200c9a66"))).status().code(), is(303));
        assertFalse(logger.hasLogged);
    }

    @Test
    public void scheduleListCanBeRequestedAsJson() throws Exception {
        final ApplicationBuilder application = application().
                add((RequestScopedModule) container ->
                        container.addInstance(UtterlyIdleRecords.class, new UtterlyIdleRecords(new MemoryRecords()))).
                add(new ScheduleModule());
        assertThat(application.handle(get("schedules/list")).entity().toString(), is("{\"schedules\":[],\"schedulerIsRunning\":false}"));
    }

    private static class StubHttpScheduler extends HttpScheduler {

        public Schedule lastSchedule;

        public StubHttpScheduler() {
            super(null, null, null, null, null);
        }

        @Override
        public UUID schedule(final Schedule schedule) {
            this.lastSchedule = schedule;
            return UUID.randomUUID();
        }

    }

    private Redirector stubRedirector() {
        return new Redirector() {
            @Override
            public Response seeOther(final Uri relativeUri) {
                return noResponse();
            }

            private Response noResponse() {
                return ResponseBuilder.response().build();
            }

            @Override
            public Response seeOther(final Invocation invocation) {
                return noResponse();
            }

            @Override
            public Response seeOther(final Binding binding, final Object... arguments) {
                return noResponse();
            }

            @Override
            public Uri uriOf(final Invocation invocation) {
                return noUri();
            }

            private Uri noUri() {
                return Uri.uri("/");
            }

            @Override
            public Uri uriOf(final Binding binding, final Object... arguments) {
                return noUri();
            }

            @Override
            public Uri absoluteUriOf(final Uri relativeUri) {
                return noUri();
            }

            @Override
            public Uri absoluteUriOf(final Invocation invocation) {
                return noUri();
            }

            @Override
            public Uri absoluteUriOf(final Binding binding, final Object... arguments) {
                return noUri();
            }

            @Override
            public Uri resourceUriOf(final Invocation invocation) {
                return noUri();
            }

            @Override
            public Uri resourceUriOf(final Binding binding, final Object... arguments) {
                return noUri();
            }
        };
    }

}