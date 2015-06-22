package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.Extractor;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNullValue;

public class PriorityExtractor implements Extractor<Method, Integer> {
    public Integer extract(Method method) {
        return Sequences.sequence(method.getAnnotation(Priority.class), method.getDeclaringClass().getAnnotation(Priority.class)).
                find(notNullValue()).map(value()).getOrElse(Priority.Medium);
    }

    private Function1<Priority, Integer> value() {
        return Priority::value;
    }
}
