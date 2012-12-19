package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Block;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public interface StreamingWriter {
    void write(Writer writer) throws IOException;

    public static class functions {
        public static Block<OutputStreamWriter> write(final StreamingWriter entity) {
            return new Block<OutputStreamWriter>() {
                @Override
                protected void execute(OutputStreamWriter writer) throws Exception {
                    entity.write(writer);
                }
            };
        }
    }
}