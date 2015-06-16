package com.googlecode.utterlyidle.bindings.actions;

import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.yadic.Container;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.LazyException.lazyException;
import static com.googlecode.totallylazy.Sequences.sequence;

public interface Action {
    String description();
    Object invoke(Container container) throws Exception;
    Iterable<ActionMetaData> metaData();

    public static class functions{
        public static Function<Action, Sequence<ActionMetaData>> metaData(){
            return new Function<Action, Sequence<ActionMetaData>>() {
                @Override
                public Sequence<ActionMetaData> call(Action action) throws Exception {
                    return sequence(action.metaData());
                }
            };
        }

        public static <T extends ActionMetaData> Function<Action, Sequence<T>> metaData(final Class<T> type){
            return new Function<Action, Sequence<T>>() {
                @Override
                public Sequence<T> call(Action action) throws Exception {
                    return metaData().call(action).safeCast(type);
                }
            };
        }

        public static LogicalPredicate<Action> isForMethod(final Method method) {
            return new LogicalPredicate<Action>() {
                @Override
                public boolean matches(Action action) {
                    try {
                        return metaData().call(action).safeCast(ResourceMethod.class).exists(ResourceMethod.functions.isForMethod(method));
                    } catch (Exception e) {
                        throw lazyException(e);
                    }
                }
            };
        }
    }
}
