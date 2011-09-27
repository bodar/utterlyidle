package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Classes.isInstance;
import static com.googlecode.totallylazy.Methods.methods;
import static com.googlecode.totallylazy.Runnables.VOID;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.yadic.resolvers.Resolvers.asCallable1;

public class Modules implements ModuleDefinitions, ModuleActivator {
    private final List<Module> modules = new ArrayList<Module>();
    private final List<Class<? extends Module>> applicationModuleClasses = new ArrayList<Class<? extends Module>>();
    private final List<Class<? extends Module>> requestModuleClasses = new ArrayList<Class<? extends Module>>();
    private final List<Class<? extends Module>> argumentModuleClasses = new ArrayList<Class<? extends Module>>();

    public Modules setupApplicationScope(Container applicationScope) {
        applicationScope.addInstance(Modules.class, this);
        applicationScope.addActivator(ModuleDefinitions.class, applicationScope.getActivator(Modules.class));
        setup(applicationScope);
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
        setup(requestScope);
        sequence(modules).forEach(activate(requestScope, requestModuleClasses));
        return this;
    }

    public ModuleActivator activateArgumentModules(Container argumentScope) {
        setup(argumentScope);
        sequence(modules).forEach(activate(argumentScope, argumentModuleClasses));
        return this;
    }

    public static Callable1<Module, Void> activate(final Container container, final Iterable<Class<? extends Module>> modules) {
        return new Callable1<Module, Void>() {
            public Void call(Module module) throws Exception {
                activate(module, container, modules);
                return VOID;
            }
        };
    }

    public static void activate(Module module, Resolver resolver, final Iterable<Class<? extends Module>> classes) {
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

    private static Object[] convertToInstances(Type[] genericParameterTypes, Resolver resolver) {
        return sequence(genericParameterTypes).map(asCallable1(resolver)).toArray(Object.class);
    }

    private static void setup(Container container) {
        container.addInstance(Container.class, container);
        container.addActivator(Resolver.class, container.getActivator(Container.class));
    }


    public static Module applicationScopedClass(final Class<?> aClass) {
        return new ApplicationScopedModule() {
            public Module addPerApplicationObjects(Container container) {
                container.add(aClass);
                return this;
            }
        };
    }

    public static Module bindingsModule(final Binding... bindings) {
        return new ResourcesModule() {
            public Module addResources(Resources resources) {
                resources.add(bindings);
                return this;
            }
        };
    }

    public static Module requestInstance(final Object instance) {
        return new RequestScopedModule() {
            public Module addPerRequestObjects(Container container) {
                Class aClass = instance.getClass();
                container.addInstance(aClass, instance);
                return this;
            }
        };
    }

    public static Module requestScopedClass(final Class<?> aClass) {
        return new RequestScopedModule() {
            public Module addPerRequestObjects(Container container) {
                container.add(aClass);
                return this;
            }
        };
    }
}
