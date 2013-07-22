package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.services.Services;
import com.googlecode.utterlyidle.services.ServicesModule;
import com.googlecode.yadic.Container;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;

public class JobsModule implements ResourcesModule, ApplicationScopedModule, RequestScopedModule, ServicesModule {
    public Resources addResources(Resources resources) throws Exception {
        return resources.
                add(annotatedClass(JobsResource.class)).
                add(annotatedClass(BatchJobsResource.class));
    }

    public Container addPerRequestObjects(Container container) throws Exception {
        return container.
                add(HttpScheduler.class).
                add(Jobs.class);
    }

    public Container addPerApplicationObjects(Container container) throws Exception {
        return container.
                add(FixedScheduler.class).
                addActivator(Scheduler.class, container.getActivator(FixedScheduler.class));
    }

    @Override
    public Services add(Services services) throws Exception {
        return services.add(FixedScheduler.class).add(HttpScheduler.class);
    }
}