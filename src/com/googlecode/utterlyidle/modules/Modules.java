package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.Runnable1;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.yadic.Container;

public class Modules {
    public static Runnable1<ResponseHandlersModule> addResponseHandlers(final ResponseHandlers registry) {
        return new Runnable1<ResponseHandlersModule>() {
            public void run(ResponseHandlersModule responseHandlersModule) {
                responseHandlersModule.addResponseHandlers(registry);
            }
        };
    }

    public static Runnable1<ResourcesModule> addResources(final Resources resources) {
        return new Runnable1<ResourcesModule>() {
            public void run(ResourcesModule resourcesModule) {
                resourcesModule.addResources(resources);
            }
        };
    }

    public static Runnable1<ApplicationScopedModule> addPerApplicationObjects(final Container applicationScope) {
        return new Runnable1<ApplicationScopedModule>() {
            public void run(ApplicationScopedModule applicationScopedModule) {
                applicationScopedModule.addPerApplicationObjects(applicationScope);
            }
        };
    }

    public static Runnable1<RequestScopedModule> addPerRequestObjects(final Container requestScope) {
        return new Runnable1<RequestScopedModule>() {
            public void run(RequestScopedModule requestScopedModule) {
                requestScopedModule.addPerRequestObjects(requestScope);
            }
        };
    }
}
