package org.webfabric.rest

import org.webfabric.collections.Iterable.convertEnumeration
import javax.servlet.http.HttpServletRequest

class QueryParameters extends Parameters

object QueryParameters{
  def apply(pairs: (String, String)*): QueryParameters = {
    val result = new QueryParameters
    pairs.foreach(pair => result.add(pair._1, pair._2))
    result
  }

  def apply(request: HttpServletRequest):QueryParameters = {
    val result = new QueryParameters
    request.getParameterNames.foreach(n => {
      val name = n.asInstanceOf[String]
      request.getParameterValues(name).foreach(v => result.add(name, v.asInstanceOf[String]))
    })
    result
  }
}