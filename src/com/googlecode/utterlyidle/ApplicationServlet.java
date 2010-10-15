package com.googlecode.utterlyidle;

import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import static com.googlecode.utterlyidle.Request.request;
import static com.googlecode.utterlyidle.Response.response;

public class ApplicationServlet extends HttpServlet{
  RequestHandler application = null;

  @Override
  public void init(ServletConfig config) {
    application = (RequestHandler) config.getServletContext().getAttribute(Application.class.getCanonicalName());
  }

  @Override
  public void service(HttpServletRequest req, HttpServletResponse resp){
    application.handle(request(req), response(resp));
  }
}