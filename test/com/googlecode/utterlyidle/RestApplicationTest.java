package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import org.junit.Before;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestApplicationTest {


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

}
