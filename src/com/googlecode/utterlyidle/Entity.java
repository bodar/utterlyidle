package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Block;
import com.googlecode.totallylazy.Characters;
import com.googlecode.totallylazy.Closeables;
import com.googlecode.totallylazy.functions.Lazy;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.numbers.Numbers;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Strings.string;
import static com.googlecode.totallylazy.matchers.NumberMatcher.greaterThan;
import static com.googlecode.utterlyidle.Entities.inputStreamOf;

public class Entity implements Value<Object>, Closeable {
    public static final Charset DEFAULT_CHARACTER_SET = Characters.UTF8;
    private static final Entity EMPTY = entity("");
    private Object value;
    private Block<OutputStream> writer;

    private Entity(Object value, Block<OutputStream> writer) {
        this.value = value;
        this.writer = writer;
    }

    public static Entity entity(Object value) {
        if (value instanceof Entity) {
            return (Entity) value;
        }
        return value == null ? empty() : new Entity(value, Entities.writerFor(value));
    }

    public static Entity empty() {
        return EMPTY;
    }

    @Override
    public Object value() {
        return value;
    }

    public String toString() {
        return string(asBytes());
    }

    public byte[] asBytes() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writer.apply(outputStream);
        byte[] bytes = outputStream.toByteArray();
        value = bytes;
        writer = Entities.writerFor(value);
        return bytes;
    }

    public boolean isStreaming() {
        return value instanceof StreamingWriter || value instanceof StreamingOutput || value instanceof InputStream;
    }

    public Block<OutputStream> writer() {
        return writer;
    }

    public Entity writer(Block<OutputStream> writer){
        this.writer = writer;
        return this;
    }


    private final Lazy<InputStream> inputStream = new Lazy<InputStream>() {
        @Override
        protected InputStream get() throws Exception {
            if (value instanceof byte[]) return inputStreamOf((byte[]) value);
            if (value instanceof InputStream) return (InputStream) value;
            if (value instanceof String) return inputStreamOf((String) value);
            throw new UnsupportedOperationException("Unsupported entity type: " + value.getClass());
        }
    };

    public InputStream inputStream() { return inputStream.apply(); }

    public Option<Integer> length() {
        if(isStreaming()) return none();
        return some(asBytes().length);
    }

    @Override
    public void close() throws IOException {
        Closeables.safeClose(value);
    }
}
