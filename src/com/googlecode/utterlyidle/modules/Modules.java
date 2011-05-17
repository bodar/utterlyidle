package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Runnables;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.yadic.Container;

import static com.googlecode.totallylazy.Runnables.VOID;

public class Modules {
    public static Callable1<ResponseHandlersModule, Void> addResponseHandlers(final ResponseHandlers registry) {
        return new Callable1<ResponseHandlersModule, Void>() {
            public Void call(ResponseHandlersModule responseHandlersModule) {
                responseHandlersModule.addResponseHandlers(registry);
                return VOID;
            }
        };
    }

    public static Callable1<ResourcesModule, Void> addResources(final Resources resources) {
        return new Callable1<ResourcesModule, Void>() {
            public Void call(ResourcesModule resourcesModule) {
                resourcesModule.addResources(resources);
                return VOID;
            }
        };
    }

    public static Callable1<ApplicationScopedModule, Void> addPerApplicationObjects(final Container applicationScope) {
        return new Callable1<ApplicationScopedModule, Void>() {
            public Void call(ApplicationScopedModule applicationScopedModule) {
                applicationScopedModule.addPerApplicationObjects(applicationScope);
                return VOID;
            }
        };
    }

    public static Callable1<RequestScopedModule, Void> addPerRequestObjects(final Container requestScope) {
        return new Callable1<RequestScopedModule, Void>() {
            public Void call(RequestScopedModule requestScopedModule) {
                requestScopedModule.addPerRequestObjects(requestScope);
                return VOID;
            }
        };
    }

    public static Callable1<ArgumentScopedModule, Void> addPerArgumentObjects(final Container argumentScope) {
        return new Callable1<ArgumentScopedModule, Void>() {
            public Void call(ArgumentScopedModule argumentScopedModule) {
                argumentScopedModule.addPerArgumentObjects(argumentScope);
                return VOID;
            }
        };
    }
}
