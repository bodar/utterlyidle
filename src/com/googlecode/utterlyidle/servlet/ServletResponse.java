package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.ContractEnforcingResponse;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

public class ServletResponse extends ContractEnforcingResponse {
    private final HttpServletResponse response;

    public ServletResponse(final HttpServletResponse response) throws IOException {
        super(getOutputStream(response));
        this.response = response;
    }

    private static Callable<OutputStream> getOutputStream(final HttpServletResponse response) {
        return new Callable<OutputStream>() {
            public OutputStream call() throws Exception {
                return response.getOutputStream();
            }
        };
    }

    @Override
    public Response status(Status value) {
        response.setStatus(value.code());
        super.status(value);
        return this;
    }

    @Override
    public Response header(String name, String value) {
        response.setHeader(name, value);
        super.header(name, value);
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
