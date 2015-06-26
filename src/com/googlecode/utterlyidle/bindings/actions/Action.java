package com.googlecode.utterlyidle.bindings.actions;

import com.googlecode.totallylazy.functions.Function1;
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
        public static Function1<Action, Sequence<ActionMetaData>> metaData(){
            return action -> sequence(action.metaData());
        }

        public static <T extends ActionMetaData> Function1<Action, Sequence<T>> metaData(final Class<T> type){
            return action -> metaData().call(action).safeCast(type);
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
