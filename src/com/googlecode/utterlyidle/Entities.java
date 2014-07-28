package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Closeables;
import com.googlecode.totallylazy.Streams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Strings.bytes;

public class Entities {
    public static final CompositeEntityWriter WRITERS = new CompositeEntityWriter();

    static {
        WRITERS.add(instanceOf(Object.class), stringEntityWriter());
        WRITERS.add(instanceOf(byte[].class), bytesEntityWriter());
        WRITERS.add(instanceOf(InputStream.class), inputStreamEntityWriter());
        WRITERS.add(instanceOf(StreamingWriter.class), streamingWriterEntityWriter());
        WRITERS.add(instanceOf(StreamingOutput.class), streamingOutputEntityWriter());
    }

    public static Block<OutputStream> writerFor(Object value) {
        return WRITERS.ruleFor(value);
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
                Closeables.using(new OutputStreamWriter(outputStream, Entity.DEFAULT_CHARACTER_SET), StreamingWriter.functions.write(entity));
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
        return inputStreamOf(value.getBytes(Entity.DEFAULT_CHARACTER_SET));
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


}
