package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class StaticMethodActivatorTest {
    @Test
    public void supportsCreatingObjectsViaStaticValueOfMethod() throws Exception {
        StaticMethodActivator activator = new StaticMethodActivator(Integer.class, containerWith("1"), String.class);
        assertThat((Integer)activator.call(), is(1));
    }

    @Test
    public void supportsCreatingObjectsViaStaticFromStringEnum() throws Exception {
        StaticMethodActivator activator = new StaticMethodActivator(TimeUnit.class, containerWith("NANOSECONDS"), String.class);
        assertThat((TimeUnit)activator.call(), is(TimeUnit.NANOSECONDS));
    }

    @Test
    public void supportsStaticFactoryMethodWithSameName() throws Exception {
        StaticMethodActivator activator = new StaticMethodActivator(MyStaticMethodClass.class, containerWith("foobar"), String.class);
        assertThat(activator.call(), is(notNullValue()));
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
