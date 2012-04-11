package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.modules.Modules;
import com.googlecode.utterlyidle.modules.StartupModule;
import com.googlecode.yadic.Container;
import org.junit.Test;

import java.util.Properties;

import static com.googlecode.utterlyidle.modules.Modules.requestInstance;
import static com.googlecode.utterlyidle.modules.Modules.requestScopedClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StartupModuleTest {
    @Test
    public void supportStartingEvenIfHasDependencyOnRequest() throws Exception {
        new RestApplication(BasePath.basePath("/")).add(requestScopedClass(StartableThing.class)).add(start());
    }

    @Test
    public void shouldNotStartIfStartingIsDisabled() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(Modules.AUTO_START, "false");
        StartableThing startable = new StartableThing(null);
        new RestApplication(BasePath.basePath("/"), properties).add(requestInstance(startable)).add(start());
        assertThat(startable.count, is(0));
    }

    @Test
    public void supportStartingViaAddMethod() throws Exception {
        StartableThing startable = new StartableThing(null);
        new RestApplication(BasePath.basePath("/")).add(requestInstance(startable)).add(start());
        assertThat(startable.count, is(1));
    }

    @Test
    public void supportStartingViaConstructor() throws Exception {
        StartableThing startable = new StartableThing(null);
        new RestApplication(BasePath.basePath("/"), requestInstance(startable), start());
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
        private final Request request;

        public StartableThing(Request request) {
            this.request = request;
        }

        public int count = 0;

        public void start() {
            count++;
        }
    }
}
