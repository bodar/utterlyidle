package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.Parameter;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.bindings.actions.InvokeResourceMethod;
import org.junit.Test;

import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_ATOM_XML;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_JSON;
import static com.googlecode.utterlyidle.UriTemplate.uriTemplate;
import static com.googlecode.utterlyidle.bindings.actions.InvokeResourceMethod.constructors.invokeResourceMethod;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.modify;
import static org.hamcrest.MatcherAssert.assertThat;

public class BindingBuilderTest {
    @Test
    public void preservesAllStateWhenModifyingABinding() throws NoSuchMethodException {
        InvokeResourceMethod action = invokeResourceMethod(BindingBuilderTest.class.getMethods()[0]);
        Sequence<Pair<Type,Option<Parameter>>> parameters = sequence(pair((Type) BindingBuilderTest.class, Option.some((Parameter) new DefinedParameter(String.class, ""))));

        Binding original = new Binding(
                action,
                uriTemplate(""),
                HttpMethod.POST,
                sequence(APPLICATION_ATOM_XML),
                sequence(APPLICATION_JSON),
                parameters,
                10,
                true
        );

        Binding built = modify(original).build();

        assertThat(built.action(), is(original.action()));
        assertThat(built.uriTemplate(), is(original.uriTemplate()));
        assertThat(built.httpMethod(), is(original.httpMethod()));
        assertThat(built.consumes(), is(original.consumes()));
        assertThat(built.produces(), is(original.produces()));
        assertThat(built.parameters(), is(original.parameters()));
        assertThat(built.priority(), is(original.priority()));
        assertThat(built.hidden(), is(original.hidden()));
    }
}
