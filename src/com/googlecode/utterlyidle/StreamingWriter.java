package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Runnables;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public interface StreamingWriter {
    void write(Writer writer) throws IOException;

    public static class functions {
        public static Function1<OutputStreamWriter, Void> write(final StreamingWriter entity) {
            return new Function1<OutputStreamWriter, Void>() {
                @Override
                public Void call(OutputStreamWriter writer) throws Exception {
                    entity.write(writer);
                    return Runnables.VOID;
                }
            };
        }
    }
}
