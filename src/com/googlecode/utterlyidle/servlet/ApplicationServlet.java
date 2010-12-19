package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.Application;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.googlecode.utterlyidle.servlet.ServletRequest.request;
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
}