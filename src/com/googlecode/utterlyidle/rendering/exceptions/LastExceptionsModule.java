package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.funclate.Model;
import com.googlecode.funclate.stringtemplate.EnhancedStringTemplateGroup;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;
import com.googlecode.yadic.Container;
import org.antlr.stringtemplate.NoIndentWriter;

import java.io.StringWriter;

import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.URLs.packageUrl;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;

public class LastExceptionsModule implements ResourcesModule, ApplicationScopedModule, ResponseHandlersModule {

    @Override
    public Module addPerApplicationObjects(Container container) throws Exception {
        container.addInstance(LastExceptions.class, new LastExceptions(20));
        return this;
    }

    @Override
    public Module addResources(Resources resources) throws Exception {
        resources.add(annotatedClass(LastExceptionsResource.class));
        return this;
    }

    @Override
    public Module addResponseHandlers(ResponseHandlers handlers) throws Exception {
        handlers.add(where(entity(Model.class), Predicates.<Model>instanceOf(Model.class)), renderer(new Renderer<Model>() {
            @Override
            public String render(Model model) throws Exception {
                EnhancedStringTemplateGroup group = new EnhancedStringTemplateGroup(packageUrl(LastExceptionsResource.class));
                StringWriter stringWriter = new StringWriter();
                group.getInstanceOf("lastExceptions", model.toMap()).write(new NoIndentWriter(stringWriter));
                return stringWriter.toString();
            }
        }));

        return this;
    }
}
