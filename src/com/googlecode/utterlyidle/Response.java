package com.googlecode.utterlyidle;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;
import java.io.*;

import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;

public class Response {
    private final Writer writer;
    private final OutputStream output;
    protected final HeaderParameters headers = headerParameters();
    protected Status code = Status.OK;

    public Response(Writer writer, OutputStream output) {
        this.writer = writer;
        this.output = output;
    }

    public Response(OutputStream output) {
        this(new OutputStreamWriter(output), output);
    }

    public Response() {
        this(new ByteArrayOutputStream());
    }


    public Response setCode(Status value) {
        this.code = value;
        return this;
    }

    public Response setHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public Response write(String value) throws IOException {
        writer.write(value);
        return this;
    }

    public Response flush() throws IOException {
        writer.flush();
        output.flush();
        return this;
    }


    public static Response response(final HttpServletResponse response) throws IOException {
        return new Response(response.getOutputStream()) {
            @Override
            public Response setHeader(String name, String value) {
                response.setHeader(name, value);
                headers.add(name, value);
                return this;
            }

            @Override
            public Response setCode(Status value) {
                response.setStatus(value.getStatusCode());
                code = value;
                return this;
            }
        };
    }

    public static Response response(OutputStream output) {
        return new Response(output);
    }

    public static Response response() {
        return new Response();
    }
}