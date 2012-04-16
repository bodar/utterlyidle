package com.googlecode.utterlyidle.examples;

import com.googlecode.totallylazy.Sequence;

import java.io.IOException;
import java.io.Writer;

public class SequenceRenderer implements WritingRenderer<Sequence<Number>>{
    @Override
    public void renderTo(Sequence<Number> entity, Writer writer) throws IOException {
        for (Number number : entity) {
            writer.write(number.toString() + "\n");
        }
    }
}
