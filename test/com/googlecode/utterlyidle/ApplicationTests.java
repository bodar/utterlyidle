package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.jobs.JobsModule;
import org.junit.Before;

import java.util.Properties;

public class ApplicationTests {
    protected Application application;

    @Before
    public void setup() {
        application = new RestApplication(BasePath.basePath("/"), new UtterlyIdleProperties(getProperties()));
        application.add(new JobsModule());
    }

    protected Properties getProperties() {
        return new Properties();
    }
}
