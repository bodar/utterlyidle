package com.googlecode.utterlyidle.jetty;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.CloseableCallable;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.ResourcesModule;

import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;

public class RestApplicationActivator implements CloseableCallable<Application> {
    private final Application application;
    private final Module[] modules;

    public RestApplicationActivator(Module... modules) {
        this(new RestApplication(), modules);
    }

    public RestApplicationActivator(Application application, Module... modules) {
        this.application = application;
        this.modules = modules;
    }

    public Application call() throws Exception {
        sequence(modules).foldLeft(application, addToApplication());
        return application;
    }

    public void close() throws IOException {
        application.close();
    }

    private Callable2<Application, Module, Application> addToApplication() {
        return new Callable2<Application, Module, Application>() {
            public Application call(Application application, Module resourcesModule) throws Exception {
                return application.add(resourcesModule);
            }
        };
    }
}
