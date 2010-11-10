package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.io.Url;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;

class ServletRequest extends Request {
    private final HttpServletRequest request;

    public ServletRequest(HttpServletRequest request) throws IOException {
        super(request.getMethod(), Url.url(request.getRequestURI() + queryString(request.getQueryString())), convertToHeaderParameters(request), request.getInputStream(), extractBasePath(request));
        this.request = request;
    }

    private static String queryString(String value) {
        return (value != null && value.length() > 0) ? "?" + value : "";
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

    public static BasePath extractBasePath(HttpServletRequest request) {
        return BasePath.basePath(request.getContextPath() + request.getServletPath());
    }

    public static Request request(HttpServletRequest request) {
        try {
            return new ServletRequest(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
