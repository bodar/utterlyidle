package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.LazyException.lazyException;
import static com.googlecode.totallylazy.Sequences.forwardOnly;
import static com.googlecode.utterlyidle.ClientAddress.clientAddress;
import static com.googlecode.utterlyidle.RequestEnricher.requestEnricher;
import static com.googlecode.utterlyidle.servlet.ApplicationContext.getApplication;
import static com.googlecode.utterlyidle.servlet.ApplicationContext.removeApplication;

public class ApplicationServlet extends HttpServlet {
    public static final String KEY = "application";
    private String initParameter;
    private ServletContext servletContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        initParameter = config.getInitParameter(KEY);
        servletContext = config.getServletContext();
    }

    @Override
    public void destroy() {
        try {
            application().close();
            removeApplication(getServletContext());
        } catch (IOException e) {
            throw lazyException(e);
        }
    }

    @Override
    public void service(final HttpServletRequest httpServletRequest, HttpServletResponse resp) throws ServletException {
        try {
            Response response = application().handle(request(httpServletRequest));
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
        for (Integer length : response.entity().length()) {
            resp.setContentLength(length);
        }
        using(resp.getOutputStream(), response.entity().writer());
    }

    public static Request request(HttpServletRequest servletRequest) {
        try {
            Request request = Requests.request(
                    servletRequest.getMethod(),
                    Uri.uri(servletRequest.getRequestURI() + queryString(servletRequest.getQueryString())),
                    convertToHeaderParameters(servletRequest),
                    servletRequest.getInputStream()
            );
            return requestEnricher(
                    clientAddress(servletRequest.getRemoteAddr()),
                    servletRequest.getProtocol().toLowerCase())
                    .enrich(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String queryString(String value) {
        return (value != null && value.length() > 0) ? "?" + value : "";
    }

    public static HeaderParameters convertToHeaderParameters(HttpServletRequest request) {
        HeaderParameters result = HeaderParameters.headerParameters();
        for (String name : forwardOnly(request.getHeaderNames(), String.class)) {
            for (String value : forwardOnly(request.getHeaders(name), String.class)) {
                result = result.add(name, value);
            }
        }
        return result;
    }

    private Application application() {
        return getApplication(servletContext, initParameter);
    }
}