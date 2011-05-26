package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.totallylazy.Methods.invoke;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.yadic.resolvers.Resolvers.asCallable1;

public class Modules implements ModuleDefinitions, ModuleActivator {
    private final List<Module> modules = new ArrayList<Module>();
    private final List<Class<? extends Module>> application = new ArrayList<Class<? extends Module>>();
    private final List<Class<? extends Module>> request = new ArrayList<Class<? extends Module>>();
    private final List<Class<? extends Module>> argument = new ArrayList<Class<? extends Module>>();

    public Modules setupApplicationScope(Container applicationScope) {
        applicationScope.addInstance(Modules.class, this);
        applicationScope.addActivator(ModuleDefinitions.class, applicationScope.getActivator(Modules.class));
        setup(applicationScope);
        return this;
    }

    public ModuleDefinitions addApplicationModule(Class<? extends Module> moduleClass) {
        application.add(moduleClass);
        return this;
    }

    public ModuleDefinitions addRequestModule(Class<? extends Module> moduleClass) {
        request.add(moduleClass);
        return this;
    }

    public ModuleDefinitions addArgumentModule(Class<? extends Module> moduleClass) {
        argument.add(moduleClass);
        return this;
    }

    public ModuleActivator activateApplicationModule(Module module, Container applicationScope) {
        modules.add(module);
        activate(module, applicationScope, Arrays.<Class<? extends Module>>asList(ModuleDefiner.class));
        activate(module, applicationScope, application);
        return this;
    }

    public ModuleActivator activateRequestModules(Container requestScope) {
        setup(requestScope);
        for (Module module : modules) {
            activate(module, requestScope, request);
        }
        return this;
    }

    public ModuleActivator activateArgumentModules(Container argumentScope) {
        setup(argumentScope);
        for (Module module : modules) {
            activate(module, argumentScope, argument);
        }
        return this;
    }

    private void activate(Module module, Container container, final Iterable<Class<? extends Module>> classes) {
        for (Class<? extends Module> aClass : classes) {
            if (aClass.isInstance(module)) {
                for (Method method : aClass.getMethods()) {
                    invoke(method, module, convertToInstances(method.getGenericParameterTypes(), container));
                }
            }
        }
    }

    private Object[] convertToInstances(Type[] genericParameterTypes, Container container) {
        return sequence(genericParameterTypes).map(asCallable1(container)).toArray(Object.class);
    }

    private void setup(Container container) {
        container.addInstance(Container.class, container);
        container.addActivator(Resolver.class, container.getActivator(Container.class));
    }

}
