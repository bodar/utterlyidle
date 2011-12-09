package com.googlecode.utterlyidle.profiling;

import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.handlers.Auditors;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.AuditModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.Requests.query;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;
import static com.googlecode.utterlyidle.profiling.FunclateModelRenderer.funclateModelRenderer;

public class ProfilingModule implements ResourcesModule, ResponseHandlersModule, AuditModule {

    @Override
    public Module addResources(Resources resources) throws Exception {
        resources.add(annotatedClass(ProfilingResource.class));
        return this;
    }

    @Override
    public Module addResponseHandlers(ResponseHandlers handlers) throws Exception {
        handlers.add(where(first(Request.class), hasQueryParameter("profile")), renderer(funclateModelRenderer(ProfilingResource.class)));
        return this;
    }

    @Override
    public Auditors addAuditors(Auditors auditors) throws Exception {
        auditors.add(ProfilingAuditor.class);
        return auditors;
    }

    public static LogicalPredicate<Request> hasQueryParameter(final String parameter) {
        return new LogicalPredicate<Request>() {
            @Override
            public boolean matches(Request request) {
                return query(request).contains(parameter);
            }
        };
    }
}
