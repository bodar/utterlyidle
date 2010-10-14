package org.webfabric.rest

import org.webfabric.collections.Iterable.convertEnumeration
import javax.servlet.http.HttpServletRequest

class FormParameters extends Parameters

object FormParameters{
  def apply(pairs: (String, String)*): FormParameters = {
    val result = new FormParameters
    pairs.foreach(pair => result.add(pair._1, pair._2))
    result
  }

  def apply(request: HttpServletRequest):FormParameters = {
    val result = new FormParameters
    request.getParameterNames.foreach(n => {
      val name = n.asInstanceOf[String]
      request.getParameterValues(name).foreach(v => result.add(name, v.asInstanceOf[String]))
    })
    result
  }
}