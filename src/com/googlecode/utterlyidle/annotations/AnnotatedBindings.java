package com.googlecode.utterlyidle.annotations;

import com.googlecode.utterlyidle.Binding;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotatedBindings {
    public static Binding[] annotatedClass(Class<?> aClass) {
        List<Binding> result = new ArrayList<Binding>();
        for (final Method method : aClass.getMethods()) {
            for (final HttpMethod httpMethod : new HttpMethodExtractor().extract(method)) {
                result.add(Matchers.binding(httpMethod.value(), method));
            }
        }

        return result.toArray(new Binding[]{});
    }
}
