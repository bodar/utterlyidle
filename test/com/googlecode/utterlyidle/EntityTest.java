package com.googlecode.utterlyidle;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.utterlyidle.EntityWriter.functions.writeWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EntityTest {
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
        assertThat(Entity.asString(ResponseBuilder.response().entity(streamingWriterOf("bar")).build()), is("bar"));
    }

    @Test
    public void shouldSupportStreamingOutput() throws Exception {
        assertThat(Entity.asString(ResponseBuilder.response().entity(streamingOutputOf("foobar")).build()), is("foobar"));
    }

    private StreamingOutput streamingOutputOf(final String value) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException {
                outputStream.write(value.getBytes());
            }
        };
    }

    private StreamingWriter streamingWriterOf(final String value) {
        return new StreamingWriter() {
            @Override
            public void write(Writer writer) throws IOException {
                writer.write(value);
            }
        };
    }
}