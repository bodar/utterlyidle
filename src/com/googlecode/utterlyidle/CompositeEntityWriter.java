package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Rules;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.utterlyidle.EntityWriter.functions.asFunction;

public class CompositeEntityWriter implements EntityWriter<Object> {
    private final Rules<Object,Callable1<OutputStream,Void>> rules = new Rules<Object, Callable1<OutputStream, Void>>();

    public CompositeEntityWriter() {
        add(instanceOf(byte[].class), bytesEntityWriter());
        add(instanceOf(StreamingWriter.class), streamingWriterEntityWriter());
        add(instanceOf(StreamingOutput.class), streamingOutputEntityWriter());
    }

    @Override
    public void write(Object entity, OutputStream outputStream) throws Exception {
        rules.call(entity).call(outputStream);
    }

    public <T> void add(Predicate<? super T> predicate, final EntityWriter<? super T> writer) {
        Predicate<Object> cast = cast(predicate);
        EntityWriter<Object> objectWriter = cast(writer);
        rules.add(cast, asFunction(objectWriter));
    }

    private EntityWriter<StreamingOutput> streamingOutputEntityWriter() {
        return new EntityWriter<StreamingOutput>() {
            @Override
            public void write(StreamingOutput entity, OutputStream outputStream) throws Exception {
                entity.write(outputStream);
            }
        };
    }

    private EntityWriter<StreamingWriter> streamingWriterEntityWriter() {
        return new EntityWriter<StreamingWriter>() {
            @Override
            public void write(StreamingWriter entity, OutputStream outputStream) throws Exception {
                using(new OutputStreamWriter(outputStream), StreamingWriter.functions.write(entity));
            }

        };
    }

    public static EntityWriter<byte[]> bytesEntityWriter() {
        return new EntityWriter<byte[]>() {
            @Override
            public void write(byte[] entity, OutputStream outputStream) throws IOException {
                outputStream.write(entity);
            }
        };
    }
}
