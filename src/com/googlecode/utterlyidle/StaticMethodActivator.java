package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.characters;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.Character.toLowerCase;

public class StaticMethodActivator implements Callable {
    private final Class aClass;
    private final String value;

    public StaticMethodActivator(Class aClass, String value) {
        this.aClass = aClass;
        this.value = value;
    }

    public Object call() throws Exception {
        return sequence("valueOf", getFactoryMethodName()).pick(new Callable1<String, Option<Object>>() {
            public Option<Object> call(String name) throws Exception {
                try {
                    return some(aClass.getMethod(name, String.class).invoke(null, value));
                } catch (NoSuchMethodException e) {
                    return none();
                } catch (NullPointerException fromNonStaticMethod) {
                    return none();
                }
            }
        });
    }

    private String getFactoryMethodName() {
        final Sequence<Character> characters = characters(aClass.getSimpleName());
        return characters.tail().cons(toLowerCase(characters.head())).toString("");
    }
}
