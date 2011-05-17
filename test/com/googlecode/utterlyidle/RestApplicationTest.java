package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.googlecode.yadic.Container;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.Closeable;
import java.io.IOException;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestApplicationTest {


    @Test
    public void willCloseApplicationScopedInstances() throws Exception {
        Application application = new TestApplication();
        application.add(new ApplicationScopedModule() {
            public Module addPerApplicationObjects(Container container) {
                container.add(CloseCounter.class);
                return this;
            }
        });
        assertThat(runningInstances(), is(0));
        application.applicationScope().get(CloseCounter.class);
        assertThat(runningInstances(), is(1));
        application.close();
        assertThat(runningInstances(), is(0));
    }
    
    @Test
    public void willCloseRequestScopedInstances() throws Exception {
        Application application = new TestApplication();
        application.add(new RequestScopedModule() {
            public Module addPerRequestObjects(Container container) {
                container.add(CloseCounter.class);
                return this;
            }
        }).add(new SingleResourceModule(DependsOnCloseCounter.class));
        assertThat(started[0], is(0));
        assertThat(stopped[0], is(0));
        application.handle(get("/foo").build());
        assertThat(started[0], is(1));
        assertThat(stopped[0], is(1));
    }

    private int runningInstances(){
        return started[0] - stopped[0];
    }

    @Before
    public void setUp() throws Exception {
        started[0] = 0;
        stopped[0] = 0;
    }

    private static final int[] started = new int[]{0};
    private static final int[] stopped = new int[]{0};
    public static class CloseCounter implements Closeable{
        public CloseCounter() {
            started[0]++;
        }

        public void close() throws IOException {
            stopped[0]++;
        }
    }

    public static  class DependsOnCloseCounter {
        private final CloseCounter counter;

        public DependsOnCloseCounter(CloseCounter counter) {
            this.counter = counter;
        }

        @GET
        @Path("foo")
        public void get(){}
    }
}
