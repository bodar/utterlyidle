package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.predicates.Predicates;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.totallylazy.template.Templates;
import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;
import com.googlecode.yadic.Container;

import java.util.Map;

import static com.googlecode.totallylazy.functions.Callables.first;
import static com.googlecode.totallylazy.predicates.Predicates.and;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.totallylazy.Strings.contains;
import static com.googlecode.utterlyidle.Requests.pathAsString;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;

public class LastExceptionsModule implements ResourcesModule, ApplicationScopedModule, ResponseHandlersModule {

    @Override
    public Container addPerApplicationObjects(Container container) throws Exception {
        return container.
                add(LastExceptionsSize.class).
                add(LastExceptions.class);
    }

    @Override
    public Resources addResources(Resources resources) throws Exception {
        return resources.add(annotatedClass(LastExceptionsResource.class));
    }

    @Override
    public ResponseHandlers addResponseHandlers(ResponseHandlers handlers) throws Exception {
        return handlers.add(isAModel().and(where(first(Request.class), and(where(pathAsString(), contains(LastExceptionsResource.PATH))))), renderer(lastExceptionsRenderer()));
    }

    private Renderer<Map<String,Object>> lastExceptionsRenderer() {
        return model -> {
            Templates group = Templates.defaultTemplates(LastExceptionsResource.class);
            return group.get("lastExceptions").render(model);
        };
    }

    private LogicalPredicate<Pair<Request, Response>> isAModel() {
        return where(entity(Map.class), Predicates.instanceOf(Map.class));
    }
}
