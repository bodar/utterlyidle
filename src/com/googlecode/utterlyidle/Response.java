package com.googlecode.utterlyidle;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FilterOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;

public class Response implements Closeable, Flushable {
    private OutputStream output;
    protected final HeaderParameters headers = headerParameters();
    protected Status code = Status.OK;
    private PrintWriter writer;

    public Response(OutputStream output) {
        this.output = output;
    }

    public Response() {
        this(new ByteArrayOutputStream());
    }

    public static Response response(OutputStream output) {
        return new Response(output);
    }

    public static Response response() {
        return new Response();
    }

    public Status code() {
        return code;
    }

    public Response code(Status value) {
        this.code = value;
        return this;
    }

    public Response header(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public HeaderParameters headers() {
        return headers;
    }

    public OutputStream output() {
        return output;
    }

    public Response output(OutputStream outputStream) {
        output = outputStream;
        writer = null;
        return this;
    }

    public PrintWriter writer() {
        if(writer== null){
            writer = new PrintWriter(output());
        }
        return writer;
    }

    public Response write(String value) throws IOException {
        writer().write(value);
        return this;
    }

    public void flush() throws IOException {
        writer().flush();
        output().flush();
    }

    public void close() throws IOException {
        flush();
        writer().close();
        output().close();
    }

}