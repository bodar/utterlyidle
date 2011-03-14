package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;

public class PriorityExtractor implements Extractor<Method, Integer>{
    private final Method method;

    public PriorityExtractor(Method method) {
        this.method = method;
    }

    public Integer extract(Method method) {
        return sequence(method.getAnnotation(Priority.class), method.getDeclaringClass().getAnnotation(Priority.class)).
                find(notNullValue()).map(value()).getOrElse(Priority.Medium);
    }

    private Callable1<Priority, Integer> value() {
        return new Callable1<Priority, Integer>() {
            public Integer call(Priority priority) throws Exception {
                return priority.value();
            }
        };
    }
}
