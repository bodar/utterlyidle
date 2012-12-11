package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.UtterlyIdleProperties;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.utterlyidle.services.Services;
import com.googlecode.utterlyidle.services.ServicesModule;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.totallylazy.Classes.isInstance;
import static com.googlecode.totallylazy.Methods.methods;
import static com.googlecode.totallylazy.Runnables.VOID;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.yadic.Containers.selfRegister;
import static com.googlecode.yadic.resolvers.Resolvers.asCallable1;

public class Modules implements ModuleDefinitions, ModuleActivator {
    public static final String AUTO_START = "auto.start";
    private final List<Module> modules = new CopyOnWriteArrayList<Module>();
    private final List<Class<? extends Module>> applicationModuleClasses = new CopyOnWriteArrayList<Class<? extends Module>>();
    private final List<Class<? extends Module>> requestModuleClasses = new CopyOnWriteArrayList<Class<? extends Module>>();
    private final List<Class<? extends Module>> argumentModuleClasses = new CopyOnWriteArrayList<Class<? extends Module>>();
    private final UtterlyIdleProperties properties;

    public Modules(UtterlyIdleProperties properties) {
        this.properties = properties;
    }

    public Modules setupApplicationScope(Container applicationScope) {
        applicationScope.addInstance(Modules.class, this);
        applicationScope.addActivator(ModuleDefinitions.class, applicationScope.getActivator(Modules.class));
        selfRegister(applicationScope);
        return this;
    }

    public ModuleDefinitions addApplicationModule(Class<? extends Module> moduleClass) {
        applicationModuleClasses.add(moduleClass);
        return this;
    }

    public ModuleDefinitions addRequestModule(Class<? extends Module> moduleClass) {
        requestModuleClasses.add(moduleClass);
        return this;
    }

    public ModuleDefinitions addArgumentModule(Class<? extends Module> moduleClass) {
        argumentModuleClasses.add(moduleClass);
        return this;
    }

    public ModuleActivator activateApplicationModule(Module module, Container applicationScope) {
        modules.add(module);
        activate(module, applicationScope, Sequences.<Class<? extends Module>>sequence(ModuleDefiner.class).join(applicationModuleClasses));
        return this;
    }

    public ModuleActivator activateRequestModules(Container requestScope) {
        selfRegister(requestScope);
        sequence(modules).each(activate(requestScope, requestModuleClasses));
        return this;
    }

    public ModuleActivator activateArgumentModules(Container argumentScope) {
        selfRegister(argumentScope);
        sequence(modules).each(activate(argumentScope, argumentModuleClasses));
        return this;
    }

    public void activateStartupModule(Module module, Container requestScope) {
        if(Boolean.valueOf(properties.getProperty(AUTO_START, "true"))){
            activate(module, requestScope, sequence(StartupModule.class));
        }
    }


    public static <M extends Iterable<? extends Class<? extends Module>>> Callable1<Module, Void> activate(final Container container, final M modules) {
        return new Callable1<Module, Void>() {
            public Void call(Module module) throws Exception {
                activate(module, container, modules);
                return VOID;
            }
        };
    }

    public static <M extends Iterable<? extends Class<? extends Module>>> void activate(Module module, Resolver resolver, final M classes) {
        sequence(classes).
                filter(isInstance(module)).
                flatMap(methods()).
                forEach(invoke(module, resolver));
    }

    private static Callable1<Method, Void> invoke(final Object instance, final Resolver resolver) {
        return new Callable1<Method, Void>() {
            public Void call(Method method) throws Exception {
                method.invoke(instance, convertToInstances(method.getGenericParameterTypes(), resolver));
                return VOID;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static Object[] convertToInstances(Type[] genericParameterTypes, Resolver resolver) {
        return sequence(genericParameterTypes).map(asCallable1(resolver)).toArray(Object.class);
    }

    public static Module applicationScopedClass(final Class<?> aClass) {
        return new ApplicationScopedModule() {
            public Container addPerApplicationObjects(Container container) {
                return container.add(aClass);
            }
        };
    }

    public static Module bindingsModule(final Binding... bindings) {
        return new ResourcesModule() {
            public Resources addResources(Resources resources) {
                return resources.add(bindings);
            }
        };
    }

    public static Module requestInstance(final Object instance) {
        return new RequestScopedModule() {
            public Container addPerRequestObjects(Container container) {
                Class aClass = instance.getClass();
                return container.addInstance(aClass, instance);
            }
        };
    }

    public static Module applicationInstance(final Object instance) {
        return new ApplicationScopedModule() {
            public Container addPerApplicationObjects(Container container) {
                Class aClass = instance.getClass();
                return container.addInstance(aClass, instance);
            }
        };
    }

    public static Module requestScopedClass(final Class<?> aClass) {
        return new RequestScopedModule() {
            public Container addPerRequestObjects(Container container) {
                return container.add(aClass);
            }
        };
    }

    public static ServicesModule serviceClass(final Class<? extends Service> aClass) {
        return new AutoRegisteringServicesModule(aClass);
    }

    private static class AutoRegisteringServicesModule implements ServicesModule, ApplicationScopedModule {
        private final Class<? extends Service> aClass;

        public AutoRegisteringServicesModule(Class<? extends Service> aClass) {
            this.aClass = aClass;
        }

        @Override
        public Services add(Services services) throws Exception {
            return services.addService(aClass);
        }

        @Override
        public Container addPerApplicationObjects(Container container) throws Exception {
            return container.add(aClass);
        }
    }
}
