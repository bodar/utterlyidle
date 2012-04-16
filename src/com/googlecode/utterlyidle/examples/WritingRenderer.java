package com.googlecode.utterlyidle.examples;

import java.io.IOException;
import java.io.Writer;

public interface WritingRenderer<T> {
    void renderTo(T entity, Writer writer) throws IOException;
}
