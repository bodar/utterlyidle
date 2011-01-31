package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.io.Url;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Requests.getBytes;
import static com.googlecode.utterlyidle.io.Url.url;
import static com.googlecode.utterlyidle.servlet.ServletResponse.response;

public class ApplicationServlet extends HttpServlet {
    Application application = null;

    @Override
    public void init(ServletConfig config) {
        application = (Application) config.getServletContext().getAttribute(Application.class.getCanonicalName());
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            application.handle(request(req), response(resp));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Request request(HttpServletRequest request) {
        try {
            return Requests.request(
                    request.getMethod(),
                    url(request.getRequestURI() + queryString(request.getQueryString())),
                    convertToHeaderParameters(request),
                    getBytes(request.getInputStream()),
                    extractBasePath(request));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private static BasePath extractBasePath(HttpServletRequest request) {
        return BasePath.basePath(request.getContextPath() + request.getServletPath());
    }

}