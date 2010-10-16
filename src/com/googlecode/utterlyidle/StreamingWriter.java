package com.googlecode.utterlyidle;

import java.io.IOException;
import java.io.Writer;

public interface StreamingWriter {
    void write(Writer writer) throws IOException;
}
