package com.googlecode.utterlyidle;

import java.io.IOException;

public interface StreamingOutput  {
    void write(java.io.OutputStream outputStream) throws IOException;
}
