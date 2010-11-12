package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.proxy.Call;
import com.googlecode.totallylazy.proxy.CallOn;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.notNull;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;

public class PriorityExtractor implements Extractor<Method, Integer>{
    private final Method method;

    public PriorityExtractor(Method method) {
        this.method = method;
    }

    public Integer extract(Method method) {
        return sequence(method.getAnnotation(Priority.class), method.getDeclaringClass().getAnnotation(Priority.class)).
                find(notNull(Priority.class)).map(value()).getOrElse(Priority.Medium);
    }

    private Callable1<Priority, Integer> value() {
        return new Callable1<Priority, Integer>() {
            public Integer call(Priority priority) throws Exception {
                return priority.value();
            }
        };
    }
}
