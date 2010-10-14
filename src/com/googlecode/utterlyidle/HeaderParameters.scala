package org.webfabric.rest

import org.webfabric.collections.Iterable.convertEnumeration
import javax.servlet.http.HttpServletRequest

class HeaderParameters extends Parameters

object HeaderParameters{
  def apply(pairs: (String, String)*): HeaderParameters = {
    val result = new HeaderParameters
    pairs.foreach(pair => result.add(pair._1, pair._2))
    result
  }

  def apply(request: HttpServletRequest):HeaderParameters = {
    val result = new HeaderParameters
    request.getHeaderNames.foreach(n => {
      val name = n.asInstanceOf[String]
      request.getHeaders(name).foreach(v => result.add(name, v.asInstanceOf[String]))
    })
    result
  }
}