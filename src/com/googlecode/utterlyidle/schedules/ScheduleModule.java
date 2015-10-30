package com.googlecode.utterlyidle.schedules;

import com.googlecode.totallylazy.json.Json;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;
import com.googlecode.utterlyidle.services.Services;
import com.googlecode.utterlyidle.services.ServicesModule;
import com.googlecode.yadic.Container;

import java.util.Map;

import static com.googlecode.totallylazy.predicates.Predicates.instanceOf;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.utterlyidle.PathMatcher.path;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;

public class ScheduleModule implements ResourcesModule, ApplicationScopedModule, RequestScopedModule, ServicesModule, ResponseHandlersModule {
    public Resources addResources(Resources resources) throws Exception {
        return resources.
                add(annotatedClass(ScheduleResource.class)).
                add(annotatedClass(BatchScheduleResource.class));
    }

    public Container addPerRequestObjects(Container container) throws Exception {
        return container.
                add(HttpScheduler.class).
                add(Schedules.class);
    }

    public Container addPerApplicationObjects(Container container) throws Exception {
        return container.
                add(FixedScheduler.class).
                add(SchedulerState.class).
                addActivator(Scheduler.class, container.getActivator(FixedScheduler.class));
    }

    @Override
    public ResponseHandlers addResponseHandlers(final ResponseHandlers handlers) throws Exception {
        return handlers.add(where(entity(), is(instanceOf(Map.class))).and(path(ScheduleResource.PATH)), renderer(Json::json));
    }

    @Override
    public Services add(Services services) throws Exception {
        return services.add(FixedScheduler.class).add(HttpScheduler.class);
    }
}