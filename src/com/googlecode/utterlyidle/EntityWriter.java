package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Function2;

import java.io.IOException;
import java.io.OutputStream;

import static com.googlecode.totallylazy.Runnables.VOID;

public interface EntityWriter<T> {
    void write(T entity, OutputStream outputStream) throws Exception;

    public static class functions {
        private functions(){}

        public static <T> Function2<T, OutputStream, Void> asFunction(final EntityWriter<T> writer) {
            return new Function2<T, OutputStream, Void>() {
                @Override
                public Void call(T o, OutputStream outputStream) throws Exception {
                    writer.write(o, outputStream);
                    return VOID;
                }
            };
        }

        public static <T> Function1<OutputStream, Void> writeWith(final EntityWriter<T> writer, final T entity){
            return new Function1<OutputStream, Void>() {
                @Override
                public Void call(OutputStream outputStream) throws Exception {
                    writer.write(entity, outputStream);
                    return VOID;
                }
            };
        }

    }
}
