package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Block;
import com.googlecode.totallylazy.Closeables;
import com.googlecode.totallylazy.Streams;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.googlecode.totallylazy.predicates.Predicates.instanceOf;
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
        return (entity, outputStream) -> entity.write(outputStream);
    }

    private static EntityWriter<StreamingWriter> streamingWriterEntityWriter() {
        return (entity, outputStream) -> Closeables.using(new OutputStreamWriter(outputStream, Entity.DEFAULT_CHARACTER_SET), StreamingWriter.functions.write(entity));
    }

    private static EntityWriter<byte[]> bytesEntityWriter() {
        return (entity, outputStream) -> outputStream.write(entity);
    }

    private static EntityWriter<InputStream> inputStreamEntityWriter() {
        return (input, output) -> {
            try {
                Streams.copy(input, output);
            } finally {
                Closeables.safeClose(input);
            }
        };
    }

    private static EntityWriter<String> stringEntityWriter() {
        return (entity, outputStream) -> outputStream.write(entity.getBytes(Entity.DEFAULT_CHARACTER_SET));
    }

    public static InputStream inputStreamOf(String value) {
        return inputStreamOf(value.getBytes(Entity.DEFAULT_CHARACTER_SET));
    }

    public static InputStream inputStreamOf(final byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    public static StreamingOutput streamingOutputOf(final String value) {
        return outputStream -> outputStream.write(bytes(value));
    }

    public static StreamingWriter streamingWriterOf(final String value) {
        return writer -> writer.write(value);
    }


}
