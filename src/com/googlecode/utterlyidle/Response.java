package com.googlecode.utterlyidle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;

public class Response {
    private final OutputStream output;
    protected final HeaderParameters headers = headerParameters();
    protected Status code = Status.OK;
    private OutputStreamWriter writer;

    public Response(OutputStream output) {
        this.output = output;
    }

    public Response() {
        this(new ByteArrayOutputStream());
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

    public Writer writer() {
        if(writer== null){
            writer = new OutputStreamWriter(output());
        }
        return writer;
    }

    public Response write(String value) throws IOException {
        writer().write(value);
        return this;
    }

    public Response flush() throws IOException {
        writer().flush();
        output().flush();
        output().close();
        return this;
    }

    public static Response response(OutputStream output) {
        return new Response(output);
    }

    public static Response response() {
        return new Response();
    }
}