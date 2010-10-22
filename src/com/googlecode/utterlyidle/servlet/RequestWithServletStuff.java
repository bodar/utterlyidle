package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.Request;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

class RequestWithServletStuff extends Request {
    private final HttpServletRequest request;

    public RequestWithServletStuff(HttpServletRequest request) throws IOException {
        super(request.getMethod(), request.getPathInfo(), ServletApiWrapper.headerParameters(request), ServletApiWrapper.queryParameters(request), ServletApiWrapper.formParameters(request), request.getInputStream());
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
