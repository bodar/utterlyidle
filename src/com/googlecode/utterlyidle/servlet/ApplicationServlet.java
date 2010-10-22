package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.RequestHandler;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.request;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.response;

public class ApplicationServlet extends HttpServlet {
    Application application = null;

    @Override
    public void init(ServletConfig config) {
        application = (Application) config.getServletContext().getAttribute(Application.class.getCanonicalName());
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) {
        application.handle(request(req), response(resp));
    }
}