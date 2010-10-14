package org.webfabric.rest

import java.lang.reflect.Method
import javax.ws.rs.{Consumes}
import javax.ws.rs.core.{HttpHeaders, MediaType}

class ConsumesMimeMatcher(method: Method) extends Matcher[Request] {
  lazy val mimeType: String = {
    List(method.getAnnotation(classOf[Consumes]), method.getDeclaringClass.getAnnotation(classOf[Consumes])).filter(_ != null) match {
      case x :: xs => x.value.first
      case Nil => MediaType.WILDCARD
    }
  }

  def isMatch(request: Request): Boolean = {
    if (mimeType.equals(MediaType.WILDCARD)) true else
    if (request.headers.contains(HttpHeaders.CONTENT_TYPE)) request.headers.getValue(HttpHeaders.CONTENT_TYPE).equals(mimeType) else true
  }
}