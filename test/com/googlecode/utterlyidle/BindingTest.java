package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.dsl.DslTest;
import org.junit.Test;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BindingTest {
    @Test
    public void supportsToString() throws Exception {
        Binding binding = get("/bar").resource(method(on(DslTest.Bar.class).hello())).build();
        assertThat(binding.toString(), is("GET bar -> public java.lang.String com.googlecode.utterlyidle.dsl.DslTest$Bar.hello()"));
    }

    @Test
    public void supportsEquality() throws Exception {
        Binding binding1 = get("/bar").resource(method(on(DslTest.Bar.class).hello())).build();
        Binding binding2 = get("/bar").resource(method(on(DslTest.Bar.class).hello())).build();
        Binding binding3 = get("/foo").resource(method(on(DslTest.Bar.class).hello())).build();

        assertThat(binding1.equals(binding2), is(true));
        assertThat(binding1.hashCode() == binding2.hashCode(), is(true));
        assertThat(binding1.equals(binding3), is(false));
        assertThat(binding1.hashCode() == binding3.hashCode(), is(false));
    }
}
