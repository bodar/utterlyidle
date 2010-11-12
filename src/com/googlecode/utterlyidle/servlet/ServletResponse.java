package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletResponse extends Response {
    private final HttpServletResponse response;

    public ServletResponse(HttpServletResponse response) throws IOException {
        super(response.getOutputStream());
        this.response = response;
    }

    @Override
    public Response header(String name, String value) {
        response.setHeader(name, value);
        super.header(name, value);
        return this;
    }

    @Override
    public Response code(Status value) {
        response.setStatus(value.code());
        super.code(value);
        return this;
    }

    public static Response response(final HttpServletResponse response) {
        try {
            return new ServletResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
