package com.googlecode.utterlyidle.profiling;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.handlers.Auditors;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.AuditModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;
import com.googlecode.utterlyidle.sitemesh.Decorators;
import com.googlecode.utterlyidle.sitemesh.SiteMeshModule;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.utterlyidle.Requests.hasQueryParameter;
import static com.googlecode.utterlyidle.Requests.queryParameter;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;
import static com.googlecode.utterlyidle.profiling.FunclateModelRenderer.funclateModelRenderer;
import static com.googlecode.utterlyidle.sitemesh.StaticDecoratorRule.staticRule;
import static com.googlecode.utterlyidle.sitemesh.TemplateName.templateName;

public class ProfilingModule implements ResourcesModule, ResponseHandlersModule, AuditModule, SiteMeshModule {

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

    @Override
    public Decorators addDecorators(Decorators decorators) {
        decorators.add(staticRule(where(first(Request.class), where(queryParameter("profile"), is(notNullValue()))), templateName("none")));
        return decorators;
    }

}
