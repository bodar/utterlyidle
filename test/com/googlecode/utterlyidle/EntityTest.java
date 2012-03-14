package com.googlecode.utterlyidle;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.googlecode.totallylazy.Closeables.using;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EntityTest {
    @Test
    public void shouldSupportInputStream() throws Exception {
        assertThat(ResponseBuilder.response().entity(new ByteArrayInputStream("Foo".getBytes(Entity.DEFAULT_CHARACTER_SET))).build().entity().toString(), is("Foo"));
    }

    @Test
    public void shouldSupportStrings() throws Exception {
        assertThat(ResponseBuilder.response().entity("Foo").build().entity().toString(), is("Foo"));
    }

    @Test
    public void shouldSupportByteArrays() throws Exception {
        assertThat(ResponseBuilder.response().entity("Foo".getBytes()).build().entity().toString(), is("Foo"));
    }

    @Test
    public void shouldSupportStreamingWriter() throws Exception {
        assertThat(ResponseBuilder.response().entity(Entity.streamingWriterOf("bar")).build().entity().toString(), is("bar"));
    }

    @Test
    public void shouldSupportStreamingOutput() throws Exception {
        assertThat(ResponseBuilder.response().entity(Entity.streamingOutputOf("foobar")).build().entity().toString(), is("foobar"));
    }

}