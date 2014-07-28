package com.googlecode.utterlyidle.jobs;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import org.junit.Test;

import java.text.ParseException;

import static com.googlecode.funclate.Model.persistent.parse;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Status.CREATED;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.relativeUriOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class JobsResourceTest {
    Application application = new HelloWorldApplication();

    @Test
    public void statusCodesMapCorrectly() throws Exception {
        Clock clock = new StoppedClock(date(2001, 1, 2));

        CreatedJob createdJob = CreatedJob.createJob(RequestBuilder.get("").build(), clock);
        assertThat(JobsResource.status(createdJob), is(Status.CREATED));

        RunningJob runningJob = createdJob.start(clock);
        assertThat(JobsResource.status(runningJob), is(Status.ACCEPTED));

        CompletedJob completedJob = runningJob.complete(ResponseBuilder.response().build(), clock);
        assertThat(JobsResource.status(completedJob), is(Status.OK));
    }

    @Test
    public void canCreateAJobAndInspectIt() throws Exception {
        ManualCompleter completer = stepCompleter();

        assertThat(numberOfJobs(), is(0));

        Response response = create(post("some/url").build());
        assertThat(response.status(), is(SEE_OTHER));
        String location = response.headers().getValue(HttpHeaders.LOCATION);
        assertThat(application.handle(get(location).build()).status(), is(CREATED));

        completer.job.call();

        assertThat(numberOfJobs(), is(1));

        Response job = application.handle(get(location).build());

        assertThat(job.status(), is(OK));

        try {
            Model json = parse(job.entity().toString());
            Dates.RFC3339withMilliseconds().parse(json.get("created", String.class));
            Dates.RFC3339withMilliseconds().parse(json.get("started", String.class));
            Dates.RFC3339withMilliseconds().parse(json.get("completed", String.class));
        } catch (ParseException e) {
            fail("Dates should be JSON ISO 8601 / RFC 3339 format");
        }
    }

    @Test
    public void canCreateMultipleJobsAndListThem() throws Exception {
        ManualCompleter manualCompleter = stepCompleter();
        create(post("some/url1").build());
        manualCompleter.job.call();
        create(post("some/url2").build());
        manualCompleter.job.call();

        assertThat(numberOfJobs(), is(2));
    }

    @Test
    public void canListJobsThatHaveNotBeenCompletedOrStarted() throws Exception {
        create(post("some/url1").build());
        create(post("some/url2").build());

        assertThat(numberOfJobs(), is(2));
    }

    private ManualCompleter stepCompleter() {
        application.applicationScope().remove(Completer.class);
        ManualCompleter completer = new ManualCompleter();
        application.applicationScope().addInstance(Completer.class, completer);
        return completer;
    }


    @Test
    public void canDeleteAllRunningJobs() throws Exception {
        ManualCompleter completer = stepCompleter();

        create(post("some/url").build());

        completer.job.call();

        assertThat(numberOfJobs(), is(1));

        deleteAll();
        assertThat(numberOfJobs(), is(0));
    }

    private void deleteAll() throws Exception {
        application.handle(post(relativeUriOf(method(on(JobsResource.class).deleteAll()))).build());
    }

    private int numberOfJobs() throws Exception {
        return jobsList().getValues("items").size();
    }

    private Model jobsList() throws Exception {
        Response response = application.handle(get(relativeUriOf(method(on(JobsResource.class).list()))).build());
        return parse(response.entity().toString());
    }

    private Response create(Request request) throws Exception {
        Uri resource = request.uri();
        String queuedPath = "/" + relativeUriOf(method(on(JobsResource.class).create(request, "/" + resource.path()))).toString();
        return application.handle(modify(request).uri(resource.path(queuedPath)).build());
    }
}
