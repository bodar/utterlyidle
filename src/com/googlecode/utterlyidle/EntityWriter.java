package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Block;
import com.googlecode.totallylazy.functions.Function1;

import java.io.OutputStream;

public interface EntityWriter<T> {
    void write(T entity, OutputStream outputStream) throws Exception;

    public static class functions {
        private functions() {
        }

        public static <T> Function1<T, Block<OutputStream>> asFunction(final EntityWriter<T> writer) {
            return t -> writeWith(writer, t);
        }

        public static <T> Block<OutputStream> writeWith(final EntityWriter<T> writer, final T entity) {
            return outputStream -> writer.write(entity, outputStream);
        }
    }
}
