package com.googlecode.utterlyidle;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.googlecode.totallylazy.Closeables.using;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EntityTest {
    @Test
    public void shouldSupportInputStream() throws Exception {
        assertThat(Entity.asString(ResponseBuilder.response().entity(new ByteArrayInputStream("Foo".getBytes(Entity.DEFAULT_CHARACTER_SET))).build()), is("Foo"));
    }

    @Test
    public void shouldSupportStrings() throws Exception {
        assertThat(Entity.asString(ResponseBuilder.response().entity("Foo").build()), is("Foo"));
    }

    @Test
    public void shouldSupportByteArrays() throws Exception {
        assertThat(Entity.asString(ResponseBuilder.response().entity("Foo".getBytes()).build()), is("Foo"));
    }

    @Test
    public void shouldSupportStreamingWriter() throws Exception {
        assertThat(Entity.asString(ResponseBuilder.response().entity(Entity.streamingWriterOf("bar")).build()), is("bar"));
    }

    @Test
    public void shouldSupportStreamingOutput() throws Exception {
        assertThat(Entity.asString(ResponseBuilder.response().entity(Entity.streamingOutputOf("foobar")).build()), is("foobar"));
    }

}