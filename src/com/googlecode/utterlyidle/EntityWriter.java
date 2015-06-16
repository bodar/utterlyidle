package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Function;

import java.io.OutputStream;

public interface EntityWriter<T> {
    void write(T entity, OutputStream outputStream) throws Exception;

    public static class functions {
        private functions() {
        }

        public static <T> Function<T, Block<OutputStream>> asFunction(final EntityWriter<T> writer) {
            return new Function<T, Block<OutputStream>>() {
                @Override
                public Block<OutputStream> call(T t) throws Exception {
                    return writeWith(writer, t);
                }
            };
        }

        public static <T> Block<OutputStream> writeWith(final EntityWriter<T> writer, final T entity) {
            return new Block<OutputStream>() {
                @Override
                public void execute(OutputStream outputStream) throws Exception {
                    writer.write(entity, outputStream);
                }
            };
        }
    }
}
