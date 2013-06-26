package com.googlecode.utterlyidle;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.utterlyidle.Entity.entity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EntityTest {
    @Test
    public void supportsLength() throws Exception {
        assertThat(entity("").length(), is(some(0)));
        assertThat(entity("Hello").length(), is(some(5)));
    }

    @Test
    public void shouldSupportInputStream() throws Exception {
        assertThat(entity(new ByteArrayInputStream(bytes("Foo"))).toString(), is("Foo"));
    }

    @Test
    public void shouldSupportStrings() throws Exception {
        assertThat(entity("Foo").toString(), is("Foo"));
    }

    @Test
    public void shouldSupportByteArrays() throws Exception {
        assertThat(entity("Foo".getBytes()).toString(), is("Foo"));
    }

    @Test
    public void shouldSupportStreamingWriter() throws Exception {
        assertThat(entity(Entity.streamingWriterOf("bar")).toString(), is("bar"));
    }

    @Test
    public void shouldSupportStreamingOutput() throws Exception {
        assertThat(entity(Entity.streamingOutputOf("foobar")).toString(), is("foobar"));
    }

}