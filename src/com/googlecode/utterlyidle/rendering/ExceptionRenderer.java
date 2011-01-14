package com.googlecode.utterlyidle.rendering;

import com.googlecode.utterlyidle.Renderer;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionRenderer implements Renderer<Exception> {
    public String render(Exception value) {
        StringWriter writer = new StringWriter();
        value.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
