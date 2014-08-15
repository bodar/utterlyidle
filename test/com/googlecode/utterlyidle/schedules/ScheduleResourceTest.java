package com.googlecode.utterlyidle.schedules;

import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.handlers.ApplicationId;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
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
        requestToSchedule = RequestBuilder.post("/foo/bar").query("queryParam", "value").header("myHeader", "myHeaderValue").entity("entityValue").build();
        internalRequestMarker = new InternalRequestMarker(ApplicationId.applicationId());
        scheduleResource = new ScheduleResource(stubHttpScheduler, requestToSchedule, stubRedirector(), internalRequestMarker);
    }

    @Test
    public void shouldScheduleWithQueryParameters() throws Exception {
        scheduleResource.scheduleWithQueryParams(SCHEDULE_ID, 60L, requestToSchedule.uri().toString());
        final Schedule expected = Schedule.schedule(SCHEDULE_ID).interval(60L).request(internalRequestMarker.markAsInternal(requestToSchedule).toString());
        assertThat(stubHttpScheduler.lastSchedule, is(expected));
    }

    @Test
    public void shouldScheduleWithSpecifiedStartTimeWithQueryParameters() throws Exception {
        scheduleResource.scheduleWithQueryParams(SCHEDULE_ID, "0600", 60L, requestToSchedule.uri().toString());
        final Schedule expected = Schedule.schedule(SCHEDULE_ID).start("0600").interval(60L).request(internalRequestMarker.markAsInternal(requestToSchedule).toString());
        assertThat(stubHttpScheduler.lastSchedule, is(expected));
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