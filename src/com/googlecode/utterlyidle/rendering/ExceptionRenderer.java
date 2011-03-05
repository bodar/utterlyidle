package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Runnable1;
import com.googlecode.utterlyidle.Renderer;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.googlecode.totallylazy.Exceptions.printStackTrace;
import static com.googlecode.totallylazy.Using.using;


public class ExceptionRenderer implements Renderer<Exception> {
    public String render(Exception value) {
        StringWriter writer = new StringWriter();
        using(new PrintWriter(writer), printStackTrace(value));
        return writer.toString();
    }


}
