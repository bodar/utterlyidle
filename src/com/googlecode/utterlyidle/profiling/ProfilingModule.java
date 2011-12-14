package com.googlecode.utterlyidle.profiling;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.handlers.Auditors;
import com.googlecode.utterlyidle.modules.AuditModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.sitemesh.Decorators;
import com.googlecode.utterlyidle.sitemesh.SiteMeshModule;
import com.googlecode.yadic.Container;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.utterlyidle.Requests.queryParameter;
import static com.googlecode.utterlyidle.sitemesh.StaticDecoratorRule.staticRule;
import static com.googlecode.utterlyidle.sitemesh.TemplateName.templateName;

public class ProfilingModule implements RequestScopedModule, AuditModule, SiteMeshModule {
    @Override
    public Module addPerRequestObjects(Container container) throws Exception {
        container.decorate(HttpHandler.class, ProfilingHandler.class);
        return this;
    }

    @Override
    public Auditors addAuditors(Auditors auditors) throws Exception {
        return auditors.add(ProfilingAuditor.class);
    }

    @Override
    public Decorators addDecorators(Decorators decorators) {
        return decorators.add(staticRule(where(first(Request.class), where(queryParameter("profile"), is(notNullValue()))), templateName("none")));
    }
}
