package com.googlecode.utterlyidle;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.utterlyidle.Entity.entity;
import static com.googlecode.utterlyidle.Entity.inputStreamOf;
import static com.googlecode.utterlyidle.Entity.streamingOutputOf;
import static com.googlecode.utterlyidle.Entity.streamingWriterOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

public class EntityTest {
    @Test
    public void ensureSameInputStreamIsAlwaysReturns() throws Exception {
        assertSameInputStream(entity(""));
        assertSameInputStream(entity("Hello"));
        assertSameInputStream(entity(inputStreamOf("Hello")));
    }

    private void assertSameInputStream(final Entity entity) {
        assertThat(entity.inputStream(), sameInstance(entity.inputStream()));
    }

    @Test
    public void canClose() throws Exception {
        Entity entity = entity(countClose());
        assertThat(entity.inputStream().read(), CoreMatchers.is(0));

        entity.close();

        assertThat(entity.inputStream().read(), CoreMatchers.is(1));
    }

    public static InputStream countClose() {
        return new InputStream() {
            private int closed = 0;

            @Override
            public int read() throws IOException {
                return closed;
            }

            @Override
            public void close() throws IOException {
                closed += 1;
            }
        };
    }


    @Test
    public void supportsLength() throws Exception {
        assertThat(entity("").length(), is(some(0)));
        assertThat(entity("Hello").length(), is(some(5)));
        assertThat(entity(inputStreamOf("Hello")).length(), is(none(Integer.class)));
        assertThat(entity(streamingWriterOf("Hello")).length(), is(none(Integer.class)));
        assertThat(entity(streamingOutputOf("Hello")).length(), is(none(Integer.class)));
    }

    @Test
    public void shouldSupportInputStream() throws Exception {
        assertThat(entity(inputStreamOf("Foo")).toString(), is("Foo"));
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
        assertThat(entity(streamingWriterOf("bar")).toString(), is("bar"));
    }

    @Test
    public void shouldSupportStreamingOutput() throws Exception {
        assertThat(entity(streamingOutputOf("foobar")).toString(), is("foobar"));
    }

}