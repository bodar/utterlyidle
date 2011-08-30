package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Runnables.write;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.ClientAddress.clientAddress;
import static com.googlecode.utterlyidle.HeaderParameters.withXForwardedFor;
import static com.googlecode.utterlyidle.RestApplication.handleRequest;
import static com.googlecode.utterlyidle.RestApplication.inject;
import static com.googlecode.utterlyidle.servlet.ApplicationContext.getApplication;
import static com.googlecode.utterlyidle.servlet.ApplicationContext.removeApplication;

public class ApplicationServlet extends HttpServlet {
    public static final String KEY = "application";
    private Application application = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        application = getApplication(config.getServletContext(), config.getInitParameter(KEY));
    }

    @Override
    public void destroy() {
        try {
            application.close();
            removeApplication(getServletContext());
        } catch (IOException e) {
            throw new LazyException(e);
        }
    }

    @Override
    public void service(final HttpServletRequest httpServletRequest, HttpServletResponse resp) throws ServletException {
        try {
            BasePath basePath = extractUrl(httpServletRequest);
            Response response = application.usingRequestScope(inject(basePath, handleRequest(request(httpServletRequest))));
            mapTo(response, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @SuppressWarnings({"deprecation"})
    private void mapTo(Response response, HttpServletResponse resp) throws IOException {
        Status status = response.status();
        resp.setStatus(status.code(), status.description());
        for (Pair<String, String> pair : response.headers()) {
            resp.setHeader(pair.first(), pair.second());
        }
        byte[] bytes = response.bytes();
        resp.setContentLength(bytes.length);
        using(resp.getOutputStream(), write(bytes));
    }

    private Request request(HttpServletRequest request) {
        try {
            return Requests.request(
                    request.getMethod(),
                    Uri.uri(request.getRequestURI() + queryString(request.getQueryString())),
                    withXForwardedFor(clientAddress(request.getRemoteAddr()), convertToHeaderParameters(request)),
                    bytes(request.getInputStream())
            );
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

    private static BasePath extractUrl(HttpServletRequest request) {
        return basePath(context(request.getContextPath()) + request.getServletPath());
    }

    private static String context(String contextPath) {
        return Strings.EMPTY.equals(contextPath) ? "/" : contextPath ;
    }


}