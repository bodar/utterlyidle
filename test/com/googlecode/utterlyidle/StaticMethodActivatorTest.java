package com.googlecode.utterlyidle;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class StaticMethodActivatorTest {
    @Test
    public void supportsCreatingObjectsViaStaticValueOfMethod() throws Exception {
        StaticMethodActivator activator = new StaticMethodActivator(Integer.class, "1");
        assertThat((Integer)activator.call(), is(1));
    }

    @Test
    public void supportsCreatingObjectsViaStaticFromStringEnum() throws Exception {
        StaticMethodActivator activator = new StaticMethodActivator(TimeUnit.class, "NANOSECONDS");
        assertThat((TimeUnit)activator.call(), is(TimeUnit.NANOSECONDS));
    }

    @Test
    public void supportsStaticFactoryMethodWithSameName() throws Exception {
        StaticMethodActivator activator = new StaticMethodActivator(MyStaticMethodClass.class, "foobar");
        assertThat(activator.call(), is(notNullValue()));
    }


    private static class MyStaticMethodClass {
        private MyStaticMethodClass() {}

        public static MyStaticMethodClass myStaticMethodClass(String parameter) {
            return new MyStaticMethodClass();
        }
    }

}
