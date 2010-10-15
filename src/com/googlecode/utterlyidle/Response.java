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
        return writer;
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


    public static Response response(final HttpServletResponse response) {
        try {
            return new Response(response.getOutputStream()) {
                @Override
                public Response header(String name, String value) {
                    response.setHeader(name, value);
                    super.header(name, value);
                    return this;
                }

                @Override
                public Response code(Status value) {
                    response.setStatus(value.getStatusCode());
                    super.code(value);
                    return this;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Response response(OutputStream output) {
        return new Response(output);
    }

    public static Response response() {
        return new Response();
    }
}