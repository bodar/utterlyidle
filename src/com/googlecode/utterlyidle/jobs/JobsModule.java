package com.googlecode.utterlyidle.jobs;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.jobs.schedule.Schedules;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.yadic.Container;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;

public class JobsModule implements ResourcesModule, ApplicationScopedModule {
    public Resources addResources(Resources resources) throws Exception {
        return resources.add(annotatedClass(JobsResource.class));
    }

    public Container addPerApplicationObjects(Container container) throws Exception {
        return container.
                add(Jobs.class, RequestJobs.class).
                add(Completer.class, CpuBoundedCompleter.class);
    }
}