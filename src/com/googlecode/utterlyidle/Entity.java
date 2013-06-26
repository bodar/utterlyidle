package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Characters;
import com.googlecode.totallylazy.Closeables;
import com.googlecode.totallylazy.Lazy;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Streams;
import com.googlecode.totallylazy.Value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.totallylazy.Strings.string;

public class Entity implements Value<Object>, Closeable {
    private static final Entity EMPTY = new Entity("");
    private Object value;

    private Entity(Object value) {
        this.value = value;
    }

    public static Entity entity(Object value) {
        if (value instanceof Entity) {
            return (Entity) value;
        }
        return value == null ? empty() : new Entity(value);
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
        byte[] bytes = writeTo(this, new ByteArrayOutputStream()).toByteArray();
        value = bytes;
        return bytes;
    }

    public boolean isStreaming() {
        return value instanceof StreamingWriter || value instanceof StreamingOutput || value instanceof InputStream;
    }

    public static final CompositeEntityWriter WRITERS = new CompositeEntityWriter();
    public static final Charset DEFAULT_CHARACTER_SET = Characters.UTF8;

    static {
        WRITERS.add(instanceOf(byte[].class), bytesEntityWriter());
        WRITERS.add(instanceOf(String.class), stringEntityWriter());
        WRITERS.add(instanceOf(InputStream.class), inputStreamEntityWriter());
        WRITERS.add(instanceOf(StreamingWriter.class), streamingWriterEntityWriter());
        WRITERS.add(instanceOf(StreamingOutput.class), streamingOutputEntityWriter());
    }

    public static <T extends OutputStream> T writeTo(Entity entity, T stream) {
        try {
            WRITERS.write(entity.value(), stream);
            return stream;
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unknown entity type " + empty().value().getClass());
        }
    }

    public Block<OutputStream> transferFrom() {
        return EntityWriter.functions.writeWith(WRITERS, value());
    }

    private static EntityWriter<StreamingOutput> streamingOutputEntityWriter() {
        return new EntityWriter<StreamingOutput>() {
            @Override
            public void write(StreamingOutput entity, OutputStream outputStream) throws Exception {
                entity.write(outputStream);
            }
        };
    }

    private static EntityWriter<StreamingWriter> streamingWriterEntityWriter() {
        return new EntityWriter<StreamingWriter>() {
            @Override
            public void write(StreamingWriter entity, OutputStream outputStream) throws Exception {
                using(new OutputStreamWriter(outputStream, DEFAULT_CHARACTER_SET), StreamingWriter.functions.write(entity));
            }

        };
    }

    private static EntityWriter<byte[]> bytesEntityWriter() {
        return new EntityWriter<byte[]>() {
            @Override
            public void write(byte[] entity, OutputStream outputStream) throws IOException {
                outputStream.write(entity);
            }
        };
    }

    private static EntityWriter<InputStream> inputStreamEntityWriter() {
        return new EntityWriter<InputStream>() {
            @Override
            public void write(InputStream input, OutputStream output) throws IOException {
                try {
                    Streams.copy(input, output);
                } finally {
                    Closeables.safeClose(input);
                }
            }
        };
    }

    private static EntityWriter<String> stringEntityWriter() {
        return new EntityWriter<String>() {
            @Override
            public void write(String entity, OutputStream outputStream) throws Exception {
                outputStream.write(entity.getBytes(Entity.DEFAULT_CHARACTER_SET));
            }
        };
    }

    public static InputStream inputStreamOf(String value) {
        return inputStreamOf(value.getBytes(DEFAULT_CHARACTER_SET));
    }

    public static InputStream inputStreamOf(final byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    public static StreamingOutput streamingOutputOf(final String value) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException {
                outputStream.write(bytes(value));
            }
        };
    }

    public static StreamingWriter streamingWriterOf(final String value) {
        return new StreamingWriter() {
            @Override
            public void write(Writer writer) throws IOException {
                writer.write(value);
            }
        };
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
    protected void finalize() throws Throwable {
        close();
    }

    @Override
    public void close() throws IOException {
        Closeables.safeClose(value);
    }
}
