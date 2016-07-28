package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.json.Json;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;
import com.googlecode.yadic.Container;

import java.util.Map;

import static com.googlecode.totallylazy.predicates.Predicates.instanceOf;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.utterlyidle.PathMatcher.path;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;
import static com.googlecode.yadic.Containers.addIfAbsent;

public class JobsModule implements ResourcesModule, ApplicationScopedModule, RequestScopedModule, ResponseHandlersModule {
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

    @Override
    public ResponseHandlers addResponseHandlers(final ResponseHandlers handlers, final Container requestScope) throws Exception {
        return handlers.add(where(entity(), is(instanceOf(Map.class))).and(path(JobsResource.JOBS)), renderer(Json::json));
    }
}