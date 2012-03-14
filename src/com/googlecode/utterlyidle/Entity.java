package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Predicates.instanceOf;

public class Entity implements Value<Object> {
    private static final Entity EMPTY = new Entity("");
    private final Object value;

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
        return writeTo(this, new ByteArrayOutputStream()).toString();
    }

    public byte[] asBytes() {
        return writeTo(this, new ByteArrayOutputStream()).toByteArray();
    }

    public boolean isStreaming() {
        return value instanceof StreamingWriter || value instanceof StreamingOutput;
    }


    public static final CompositeEntityWriter WRITERS = new CompositeEntityWriter();
    public static final String DEFAULT_CHARACTER_SET = "UTF-8";

    static {
        WRITERS.add(instanceOf(byte[].class), bytesEntityWriter());
        WRITERS.add(instanceOf(InputStream.class), inputStreamEntityWriter());
        WRITERS.add(instanceOf(String.class), stringEntityWriter());
        WRITERS.add(instanceOf(StreamingWriter.class), streamingWriterEntityWriter());
        WRITERS.add(instanceOf(StreamingOutput.class), streamingOutputEntityWriter());
    }

    public static <T extends OutputStream> T writeTo(Entity entity, T stream) {
        try {
            WRITERS.write(entity.value(), stream);
            return stream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Callable1<OutputStream, Void> transferFrom() {
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
                using(new OutputStreamWriter(outputStream), StreamingWriter.functions.write(entity));
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
                copy(input, output);
            }
        };
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    private static EntityWriter<String> stringEntityWriter() {
        return new EntityWriter<String>() {
            @Override
            public void write(String entity, OutputStream outputStream) throws Exception {
                outputStream.write(entity.getBytes(Entity.DEFAULT_CHARACTER_SET));
            }
        };
    }

    public static StreamingOutput streamingOutputOf(final String value) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException {
                outputStream.write(value.getBytes());
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


}
