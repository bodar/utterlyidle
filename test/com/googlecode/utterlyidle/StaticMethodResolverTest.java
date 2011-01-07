package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class StaticMethodResolverTest {
    @Test
    public void supportsCreatingObjectsViaStaticValueOfMethod() throws Exception {
        StaticMethodResolver resolver = new StaticMethodResolver(containerWith("1"), String.class);
        assertThat((Integer) resolver.resolve(Integer.class), is(1));
    }

    @Test
    public void supportsCreatingObjectsViaStaticFromStringEnum() throws Exception {
        StaticMethodResolver resolver = new StaticMethodResolver(containerWith("NANOSECONDS"), String.class);
        assertThat((TimeUnit) resolver.resolve(TimeUnit.class), is(TimeUnit.NANOSECONDS));
    }

    @Test
    public void supportsStaticFactoryMethodWithSameName() throws Exception {
        StaticMethodResolver resolver = new StaticMethodResolver(containerWith("foobar"), String.class);
        assertThat(resolver.resolve(MyStaticMethodClass.class), is(notNullValue()));
    }

    private Container containerWith(String value) {
        return new SimpleContainer().addInstance(String.class, value);
    }


    private static class MyStaticMethodClass {
        private MyStaticMethodClass() {}

        public static MyStaticMethodClass myStaticMethodClass(String parameter) {
            return new MyStaticMethodClass();
        }
    }

}
