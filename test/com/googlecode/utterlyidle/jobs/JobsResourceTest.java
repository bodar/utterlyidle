package com.googlecode.utterlyidle.jobs;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.ApplicationTests;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.funclate.Model.persistent.parse;
import static com.googlecode.totallylazy.Strings.string;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Status.ACCEPTED;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.relativeUriOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class JobsResourceTest extends ApplicationTests {
    @Test
    public void canCreateAJobAndInspectIt() throws Exception {
        ManualCompleter completer = stepCompleter();

        assertThat(numberOfCompletedJobs(), is(0));

        Response response = create(post("some/url").build());
        assertThat(response.status(), is(SEE_OTHER));
        String location = response.headers().getValue(HttpHeaders.LOCATION);
        assertThat(application.handle(get(location).build()).status(), is(ACCEPTED));

        assertThat(application.handle(get(location).build()).status(), is(ACCEPTED));

        completer.job.call();

        assertThat(numberOfCompletedJobs(), is(1));

        assertThat(application.handle(get(location).build()).status(), is(NOT_FOUND));

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

        assertThat(numberOfCompletedJobs(), is(1));

        deleteAll();
        assertThat(numberOfCompletedJobs(), is(0));
    }

    private void deleteAll() throws Exception {
        application.handle(post(relativeUriOf(method(on(JobsResource.class).deleteAll()))).build());
    }

    private int numberOfCompletedJobs() throws Exception {
        Response response = application.handle(get(relativeUriOf(method(on(JobsResource.class).list()))).build());
        Model model = parse(string(response.entity().value()));
        return model.getValues("items").size();
    }

    private Response create(Request request) throws Exception {
        Uri resource = request.uri();
        String queuedPath = "/" + relativeUriOf(method(on(JobsResource.class).create(request, "/" + resource.path()))).toString();
        return application.handle(modify(request).uri(resource.path(queuedPath)).build());
    }
}
