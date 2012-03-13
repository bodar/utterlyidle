package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Predicates.nullValue;

public class Entity {
    public static final CompositeEntityWriter WRITERS = new CompositeEntityWriter();
    public static final String DEFAULT_CHARACTER_SET = "UTF-8";

    static {
        WRITERS.add(instanceOf(byte[].class), bytesEntityWriter());
        WRITERS.add(instanceOf(String.class), stringEntityWriter());
        WRITERS.add(instanceOf(StreamingWriter.class), streamingWriterEntityWriter());
        WRITERS.add(instanceOf(StreamingOutput.class), streamingOutputEntityWriter());
    }

    public static String asString(Response response) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            writeTo(response, stream);
            return stream.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeTo(Response response, OutputStream stream) throws Exception {
        WRITERS.write(response.entity(), stream);
    }

    public static byte[] asByteArray(Response response) {
        try {
            return asString(response).getBytes(DEFAULT_CHARACTER_SET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static Callable1<OutputStream, Void> transferFrom(Response response) {
        return EntityWriter.functions.writeWith(WRITERS, response.entity());
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

    private static EntityWriter<String> stringEntityWriter() {
        return new EntityWriter<String>() {
            @Override
            public void write(String entity, OutputStream outputStream) throws Exception {
                outputStream.write(entity.getBytes(Entity.DEFAULT_CHARACTER_SET));
            }
        };
    }

}
