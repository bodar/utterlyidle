package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Block;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public interface StreamingWriter {
    void write(Writer writer) throws IOException;

    class functions {
        public static Block<Writer> write(final StreamingWriter entity) {
            return entity::write;
        }
    }
}