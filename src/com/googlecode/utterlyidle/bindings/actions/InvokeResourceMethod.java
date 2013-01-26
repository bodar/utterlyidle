package com.googlecode.utterlyidle.bindings.actions;

import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.MatchedResource;
import com.googlecode.utterlyidle.ParametersExtractor;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.bindings.MatchedBinding;
import com.googlecode.yadic.Container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Exceptions.toException;

public class InvokeResourceMethod implements Action {
    private final Method method;

    public InvokeResourceMethod(Method method) {
        if(method==null)
            throw new IllegalArgumentException("method cannot be null");
        this.method = method;
    }

    public Object invoke(Container container) throws Exception {
        Request request = container.get(Request.class);
        Application application = container.get(Application.class);
        Binding binding = container.get(MatchedBinding.class).value();
        try {
            Class<?> declaringClass = method.getDeclaringClass();

            registerMatchedResource(container, declaringClass);

            Object resourceInstance = container.get(declaringClass);
            Object[] arguments = new ParametersExtractor(binding.uriTemplate(), application, binding.parameters()).extract(request);
            return method.isVarArgs()
                    ? method.invoke(resourceInstance, (Object) arguments)
                    : method.invoke(resourceInstance, arguments);
        } catch (InvocationTargetException e) {
            throw toException(e.getCause());
        }
    }

    @Override
    public Iterable<ActionMetaData> metaData() {
        return Sequences.sequence(
                ResourceClass.constructors.resourceClass(method.getDeclaringClass()),
                ResourceMethod.constructors.resourceMethod(method))
                .safeCast(ActionMetaData.class);
    }

    private void registerMatchedResource(Container container, Class<?> declaringClass) {
        if (container.contains(MatchedResource.class)) container.remove(MatchedResource.class);
        container.addInstance(MatchedResource.class, new MatchedResource(declaringClass));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof InvokeResourceMethod && ((InvokeResourceMethod) o).method.equals(method);
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public String description() {
        return toString();
    }

    @Override
    public String toString() {
        return method.toString();
    }

    public static class constructors{
        public static InvokeResourceMethod invokeResourceMethod(Method method){
            return new InvokeResourceMethod(method);
        }
    }
}
