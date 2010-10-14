package org.webfabric.rest

import java.lang.reflect.Method
import javax.ws.rs.Path

class UriTemplateExtractor extends Extractor[Method, UriTemplate] {
  def extract(method: Method): UriTemplate = {
    val paths = List(method.getDeclaringClass.getAnnotation(classOf[Path]), method.getAnnotation(classOf[Path]))
    new UriTemplate(paths.filter(_ != null).map(_.value).mkString("/"))
  }
}