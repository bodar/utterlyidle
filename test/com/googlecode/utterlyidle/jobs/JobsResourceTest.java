package com.googlecode.utterlyidle.jobs;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.ApplicationTests;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static com.googlecode.funclate.Model.persistent.parse;
import static com.googlecode.totallylazy.Strings.string;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Status.ACCEPTED;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.relativeUriOf;
import static org.junit.Assert.assertThat;

public class JobsResourceTest extends ApplicationTests {
    @Test
    public void canRunARequest() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        application.applicationScope().
                addInstance(CountDownLatch.class, latch).
                decorate(Completer.class, CountDownCompleter.class);

        assertThat(numberOfCompletedJobs(), is(0));

        assertThat(run(post("some/url").build()).status(), is(ACCEPTED));
        latch.await();

        assertThat(numberOfCompletedJobs(), is(1));
    }

    @Test
    public void canDeleteAllRunningJobs() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        application.applicationScope().addInstance(CountDownLatch.class, latch).
                decorate(Completer.class, CountDownCompleter.class);

        run(post("some/url").build());
        latch.await();
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

    private Response run(Request request) throws Exception {
        Uri resource = request.uri();
        String queuedPath = "/" + relativeUriOf(method(on(JobsResource.class).run(request, "/" + resource.path()))).toString();
        return application.handle(modify(request).uri(resource.path(queuedPath)).build());
    }
}
