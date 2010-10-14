package org.webfabric.rest

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import javax.servlet.ServletConfig

class ApplicationServlet extends HttpServlet{
  var application:RequestHandler = null

  override def init(config: ServletConfig) = {
    application = config.getServletContext.getAttribute(classOf[Application].getCanonicalName).asInstanceOf[RequestHandler]
  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) = {
    application.handle(Request(req), Response(resp))
  }
}