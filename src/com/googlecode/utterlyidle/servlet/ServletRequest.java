package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.ResourcePath;
import com.googlecode.utterlyidle.io.Url;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;

class ServletRequest extends Request {
    private final HttpServletRequest request;

    public ServletRequest(HttpServletRequest request) throws IOException {
        super(request.getMethod(), Url.url(request.getRequestURI()), convertToHeaderParameters(request), request.getInputStream());
        this.request = request;
    }

    private static HeaderParameters convertToHeaderParameters(HttpServletRequest request) {
        HeaderParameters result = HeaderParameters.headerParameters();
        for (String name : sequence(request.getHeaderNames(), String.class)) {
            for (String value : sequence(request.getHeaders(name), String.class)) {
                result.add(name, value);
            }
        }
        return result;
    }

    public BasePath basePath() {
        return BasePath.basePath(request.getContextPath());
    }

    public ResourcePath resourcePath() {
        return ResourcePath.resourcePath(request.getServletPath() + request.getPathInfo());
    }
}
