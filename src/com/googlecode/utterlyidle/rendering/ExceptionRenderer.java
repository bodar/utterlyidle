package com.googlecode.utterlyidle.rendering;

import com.googlecode.utterlyidle.Renderer;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Exceptions.printStackTrace;


public class ExceptionRenderer implements Renderer<Exception> {
    public String render(Exception value) {
        return toString(value);
    }

    public static String toString(Throwable value) {
        StringWriter writer = new StringWriter();
        using(new PrintWriter(writer), printStackTrace(value));
        return writer.toString();
    }


}
