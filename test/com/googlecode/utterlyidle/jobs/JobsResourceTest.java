package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.json.Json;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.ExceptionLogger;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.RelativeUriExtractor.relativeUriOf;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.utterlyidle.Request.post;
import static com.googlecode.utterlyidle.Request.Builder.uri;
import static com.googlecode.utterlyidle.Status.CREATED;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.relativeUriOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class JobsResourceTest {
    private Application application = new HelloWorldApplication();
    private SpyExceptionLogger logger;

    @Before
    public void setupExceptionLogger() {
        logger = new SpyExceptionLogger();
        application.add((RequestScopedModule) container -> {
            container.remove(ExceptionLogger.class);
            return container.addInstance(ExceptionLogger.class, logger);
        });
    }

    @Test
    public void statusCodesMapCorrectly() throws Exception {
        Clock clock = new StoppedClock(date(2001, 1, 2));

        CreatedJob createdJob = CreatedJob.createJob(Request.get(""), clock);
        assertThat(JobsResource.status(createdJob), is(Status.CREATED));

        RunningJob runningJob = createdJob.start(clock);
        assertThat(JobsResource.status(runningJob), is(Status.ACCEPTED));

        CompletedJob completedJob = runningJob.complete(Response.ok(), clock);
        assertThat(JobsResource.status(completedJob), is(Status.OK));
    }

    @Test
    public void canCreateAJobAndInspectIt() throws Exception {
        ManualCompleter completer = stepCompleter();

        assertThat(numberOfJobs(), is(0));

        Response response = create(Request.post("some/url"));
        assertThat(response.status(), is(SEE_OTHER));
        String location = response.headers().getValue(HttpHeaders.LOCATION);
        assertThat(application.handle(Request.get(location)).status(), is(CREATED));

        completer.job.call();

        assertThat(numberOfJobs(), is(1));

        Response job = application.handle(Request.get(location));

        assertThat(job.status(), is(OK));

        try {
            String jsonResult = job.entity().toString();
            Map<String, Object> json = Json.map(jsonResult);
            Dates.RFC3339withMilliseconds().parse((String) json.get("created"));
            Dates.RFC3339withMilliseconds().parse((String) json.get("started"));
            Dates.RFC3339withMilliseconds().parse((String) json.get("completed"));
        } catch (ParseException e) {
            fail("Dates should be JSON ISO 8601 / RFC 3339 format");
        }
    }

    @Test
    public void canCreateMultipleJobsAndListThem() throws Exception {
        ManualCompleter manualCompleter = stepCompleter();
        create(Request.post("some/url1"));
        manualCompleter.job.call();
        create(Request.post("some/url2"));
        manualCompleter.job.call();

        assertThat(numberOfJobs(), is(2));
    }

    @Test
    public void canListJobsThatHaveNotBeenCompletedOrStarted() throws Exception {
        create(Request.post("some/url1"));
        create(Request.post("some/url2"));

        assertThat(numberOfJobs(), is(2));
    }

    @Test
    public void canDeleteAllRunningJobs() throws Exception {
        ManualCompleter completer = stepCompleter();

        create(Request.post("some/url"));

        completer.job.call();

        assertThat(numberOfJobs(), is(1));

        deleteAll();
        assertThat(numberOfJobs(), is(0));
    }

    @Test
    public void shouldNotThrowExceptionWhenListingJobsInDebugMode() throws Exception {
        jobsList();
        assertFalse(logger.hasLogged);
    }

    @Test
    public void shouldNotThrowExceptionForAnyRequestWithoutQuery() throws Exception {
        sequence(annotatedClass(JobsResource.class)).filter(getBindingWithNoArguments()).each(binding -> application.handle(Request.get(relativeUriOf(binding))));
        assertFalse(logger.hasLogged);
    }

    private ManualCompleter stepCompleter() {
        application.applicationScope().remove(Completer.class);
        ManualCompleter completer = new ManualCompleter();
        application.applicationScope().addInstance(Completer.class, completer);
        return completer;
    }

    private void deleteAll() throws Exception {
        application.handle(Request.post(relativeUriOf(method(on(JobsResource.class).deleteAll()))));
    }

    private int numberOfJobs() throws Exception {
        List<?> items = (List<?>) jobsList().get("items");
        return items.size();
    }

    private Map<String, Object> jobsList() throws Exception {
        Response response = application.handle(Request.get(relativeUriOf(method(on(JobsResource.class).list()))));
        return Json.map(response.entity().toString());
    }

    private Response create(Request request) throws Exception {
        Uri resource = request.uri();
        String queuedPath = "/" + relativeUriOf(method(on(JobsResource.class).create(request, "/" + resource.path()))).toString();
        return application.handle(modify(request, uri(resource.path(queuedPath))));
    }

    private Predicate<Binding> getBindingWithNoArguments() {
        return binding -> binding.numberOfArguments() == 0 && "GET".equalsIgnoreCase(binding.httpMethod());
    }
}
