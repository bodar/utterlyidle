package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callables;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.utterlyidle.services.Services;
import org.junit.Before;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestApplicationTest {

    @Test(expected = IllegalStateException.class )
    public void shouldThrowIfApplicationIsClosedAndThenAccessed() throws Exception {
        Application application = application().build();
        application.close();
        application.handle(get("should/not/be/handled").build());
    }

    @Test
    public void shouldNotThrowIfCloseIsCalledMultipleTimesOrShutDown() throws Exception {
        Application application = application().build();
        application.close();
        application.close();
        application.stop();
    }

    @Test
    public void willCloseApplicationScopedInstances() throws Exception {
        Application application = application().addApplicationScopedClass(CloseCounter.class).build();
        assertThat(runningInstances(), is(0));
        application.applicationScope().get(CloseCounter.class);
        assertThat(runningInstances(), is(1));
        application.close();
        assertThat(runningInstances(), is(0));
    }

    @Test
    public void willCloseRequestScopedInstances() throws Exception {
        ApplicationBuilder application = application().
                addRequestScopedClass(CloseCounter.class).
                addAnnotated(DependsOnCloseCounter.class);
        assertThat(started[0], is(0));
        assertThat(stopped[0], is(0));
        application.handle(get("/foo"));
        assertThat(started[0], is(1));
        assertThat(stopped[0], is(1));
    }

    @Test
    public void servicesAreDiscoverable() {
        Application application = application().
                addService(FooService.class).build();

        Services services = application.applicationScope().get(Services.class);
        assertThat(sequence(services).map(Callables.<Class<? extends Service>>first()).contains(FooService.class), is(true));
    }


    @Test
    public void servicesAreNotStartedOnAppConstruction() {
        Application application = application().
                addService(FooService.class).build();

        FooService service = application.applicationScope().get(FooService.class);
        assertThat(service.starts.get(), is(0));
        assertThat(service.stops.get(), is(0));
    }

    @Test
    public void startingApplicationStartsAndStopsServices() throws Exception {
        Application application = application().
                addService(FooService.class).build();
        application.start();
        FooService service = application.applicationScope().get(FooService.class);
        assertThat(service.starts.get(), is(1));
        assertThat(service.stops.get(), is(0));

        application.stop();
        assertThat(service.starts.get(), is(1));
        assertThat(service.stops.get(), is(1));
    }

    @Test
    public void basePathIsStrippedFromRequests() throws Exception {

        final Response response = new RestApplication(basePath("foo")).add((ResourcesModule) resources -> resources.add(annotatedClass(Foo.class))).handle(get("/foo/").build());

        assertThat(response.status(), is(Status.OK));
    }

    public static class Foo {
        @GET
        @Path("")
        public String respond() {
            return "foo";
        }
    }

    private int runningInstances() {
        return started[0] - stopped[0];
    }

    @Before
    public void setUp() throws Exception {
        started[0] = 0;
        stopped[0] = 0;
    }

    private static final int[] started = new int[]{0};
    private static final int[] stopped = new int[]{0};

    public static class CloseCounter implements Closeable {
        public CloseCounter() {
            started[0]++;
        }

        public void close() throws IOException {
            stopped[0]++;
        }
    }

    public static class DependsOnCloseCounter {
        private final CloseCounter counter;

        public DependsOnCloseCounter(CloseCounter counter) {
            this.counter = counter;
        }

        @GET
        @Path("foo")
        public void get() {
        }
    }

    public static class FooService implements Service {
        public final AtomicInteger starts = new AtomicInteger();
        public final AtomicInteger stops = new AtomicInteger();

        @Override
        public void start() {
            starts.incrementAndGet();
        }

        @Override
        public void stop() {
            stops.incrementAndGet();
        }
    }
}
