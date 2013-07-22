package com.googlecode.utterlyidle.jobs;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.jobs.schedule.Schedules;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.yadic.Container;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;

public class QueuesModule implements ResourcesModule, ApplicationScopedModule, RequestScopedModule {
    public Resources addResources(Resources resources) throws Exception {
        return resources.add(annotatedClass(QueuesResource.class));
    }

    public Container addPerApplicationObjects(Container container) throws Exception {
        return container.
                add(Queues.class, RequestQueues.class).
                add(Completer.class, CpuBoundedCompleter.class);
    }

    @Override
    public Container addPerRequestObjects(final Container container) throws Exception {
        return container.
                add(Schedules.class);
    }
}