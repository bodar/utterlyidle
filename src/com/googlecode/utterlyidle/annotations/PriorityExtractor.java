package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.Extractor;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNullValue;

public class PriorityExtractor implements Extractor<Method, Integer> {
    public Integer extract(Method method) {
        return Sequences.sequence(method.getAnnotation(Priority.class), method.getDeclaringClass().getAnnotation(Priority.class)).
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
