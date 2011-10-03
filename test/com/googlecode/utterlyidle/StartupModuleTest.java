package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.modules.StartupModule;
import com.googlecode.yadic.Container;
import org.junit.Test;

import static com.googlecode.utterlyidle.modules.Modules.requestInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StartupModuleTest {
    @Test
    public void supportStartingViaAddMethod() throws Exception {
        StartableThing startable = new StartableThing();
        new RestApplication().add(requestInstance(startable)).add(start());
        assertThat(startable.count, is(1));
    }

    @Test
    public void supportStartingViaConstructor() throws Exception {
        StartableThing startable = new StartableThing();
        new RestApplication(requestInstance(startable), start());
        assertThat(startable.count, is(1));
    }

    private StartupModule start() {
        return new StartupModule() {
            public Container start(Container requestScope) {
                requestScope.get(StartableThing.class).start();
                return requestScope;
            }
        };
    }

    public static class StartableThing {
        public int count = 0;

        public void start() {
            count++;
        }
    }
}
