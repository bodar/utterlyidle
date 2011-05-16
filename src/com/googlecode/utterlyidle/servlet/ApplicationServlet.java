package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.ClientAddress;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.totallylazy.Runnables.write;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.utterlyidle.ClientAddress.*;
import static com.googlecode.utterlyidle.HeaderParameters.withXForwardedFor;
import static com.googlecode.utterlyidle.ServerUrl.*;
import static com.googlecode.utterlyidle.io.Url.url;
import static java.lang.String.*;

public class ApplicationServlet extends HttpServlet {
    static final ThreadLocal<ServerUrl> serverUrl = new ThreadLocal<ServerUrl>();

    Application application = null;

    @Override
    public void init(ServletConfig config) {
        application = ApplicationStarter.getApplication(config.getServletContext());
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            serverUrl.set(extractUrl(req));
            Response response = application.handle(request(req));
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
        return serverUrl(format("%s://%s:%s%s%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(), request.getServletPath()));
    }

}