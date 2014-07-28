package com.googlecode.utterlyidle.jobs;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.yadic.Container;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.yadic.Containers.addActivatorIfAbsent;
import static com.googlecode.yadic.Containers.addIfAbsent;

public class JobsModule implements ResourcesModule, ApplicationScopedModule, RequestScopedModule {
    public Resources addResources(Resources resources) throws Exception {
        return resources.add(annotatedClass(JobsResource.class));
    }

    @Override
    public Container addPerApplicationObjects(Container container) throws Exception {
        addIfAbsent(container, JobsStorage.class, InMemoryJobsStorage.class);
        return addIfAbsent(container, JobsHistoryCapacity.class).
                add(Completer.class, CpuBoundedCompleter.class);
    }

    @Override
    public Container addPerRequestObjects(final Container container) throws Exception {
        container.add(Jobs.class, RequestJobs.class);
        addIfAbsent(container, UtterlyIdleRecords.class);
        return container;
    }
}