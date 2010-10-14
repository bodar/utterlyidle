package org.webfabric.rest

import java.io.{InputStream}
import javax.servlet.http.HttpServletRequest
import org.webfabric.servlet.{BasePath, ContextPath}

case class Request(method:String, base:BasePath, path:String, headers:HeaderParameters, query:QueryParameters, form:FormParameters, input:InputStream)

object Request{
  def apply(request: HttpServletRequest):Request = {
    Request(request.getMethod, BasePath(request), request.getPathInfo, HeaderParameters(request), QueryParameters(request), FormParameters(request), request.getInputStream)
  }

  def apply(method:String, path:String, headers:HeaderParameters, query:QueryParameters, form:FormParameters, input:InputStream):Request = {
    Request(method, BasePath(""), path, headers, query, form, input)
  }
}