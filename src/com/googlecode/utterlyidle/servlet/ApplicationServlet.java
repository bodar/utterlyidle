package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ServerUrl;
import com.googlecode.utterlyidle.Status;
import com.googlecode.yadic.Container;

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
import static com.googlecode.utterlyidle.ClientAddress.clientAddress;
import static com.googlecode.utterlyidle.HeaderParameters.withXForwardedFor;
import static com.googlecode.utterlyidle.RestApplication.handleRequest;
import static com.googlecode.utterlyidle.RestApplication.inject;
import static com.googlecode.utterlyidle.io.Url.url;
import static com.googlecode.utterlyidle.servlet.ApplicationContext.getApplication;
import static java.lang.String.format;

public class ApplicationServlet extends HttpServlet {
    public static final String KEY = "application";
    private Application application = null;

    @Override
    public void init(ServletConfig config) {
        application = getApplication(config.getServletContext(), config.getInitParameter(KEY));
    }

    @Override
    public void service(final HttpServletRequest httpServletRequest, HttpServletResponse resp) throws ServletException {
        try {
            ServerUrl serverUrl = extractUrl(httpServletRequest);
            Response response = application.usingRequestScope(inject(serverUrl, handleRequest(request(httpServletRequest))));
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
                    url(request.getRequestURI() + queryString(request.getQueryString())),
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

    private static ServerUrl extractUrl(HttpServletRequest request) {
        return ServerUrl.serverUrl(format("%s://%s:%s%s%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(), request.getServletPath()));
    }


}