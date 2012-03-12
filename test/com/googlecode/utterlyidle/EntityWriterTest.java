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

public class EntityWriterTest {
    @Test
    public void shouldSupportByteArrays() throws Exception {
        CompositeEntityWriter entityWriter = new CompositeEntityWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        using(out, writeWith(entityWriter, "Foo".getBytes()));

        assertThat(out.toString(), is("Foo"));
    }

    @Test
    public void shouldSupportStreamingWriter() throws Exception {
        CompositeEntityWriter entityWriter = new CompositeEntityWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        using(out, writeWith(entityWriter, streamingWriterOf("bar")));

        assertThat(out.toString(), is("bar"));
    }

    @Test
    public void shouldSupportStreamingOutput() throws Exception {
        CompositeEntityWriter entityWriter = new CompositeEntityWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        using(out, writeWith(entityWriter, streamingOutputOf("foobar")));

        assertThat(out.toString(), is("foobar"));
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