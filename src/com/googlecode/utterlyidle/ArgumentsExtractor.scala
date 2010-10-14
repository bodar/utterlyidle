package org.webfabric.rest

import com.googlecode.yadic.{SimpleContainer, Container}
import java.lang.reflect.Method
import java.io.{InputStream}
import javax.ws.rs.{HeaderParam, QueryParam, FormParam, PathParam}

class ArgumentsExtractor(uriTemplate: UriTemplate, method: Method) extends RequestExtractor[Array[Object]] {
  def isMatch(request: Request) = {
    try {
      extract(request)
      true
    } catch {
      case _ => false
    }
  }

  type Param = { def value():String }

  def extractParam[T <: Parameters](container: Container, param: Param, aClass:Class[T]): String = {
    val params = container.resolveType(aClass)
    if (!params.contains(param.value)) throw new NoSuchElementException
    params.getValue(param.value)
  }

  def extract(request: Request): Array[Object] = {
    val container = getArgumentContainer
    container.addInstance(request)
    method.getParameterTypes.zip(method.getParameterAnnotations).map(pair => {
      val aClass = pair._1
      val annotations = pair._2
      if (!container.contains(aClass)) container.add(aClass)
      container.remove(classOf[String])
      if (annotations.length > 0) annotations(0) match {
        case query: QueryParam => container.add(classOf[String], () => extractParam(container, query, classOf[QueryParameters]))
        case form: FormParam => container.add(classOf[String], () => extractParam(container, form, classOf[FormParameters]))
        case path: PathParam => container.add(classOf[String], () => extractParam(container, path, classOf[PathParameters]))
        case header: HeaderParam => container.add(classOf[String], () => extractParam(container, header, classOf[HeaderParameters]))
        case _ =>
      }
      container.resolve(aClass)
    })
  }

  def getArgumentContainer: Container = {
    val container = new SimpleContainer
    container.addInstance(uriTemplate)
    container.add(classOf[PathParameters], () => container.resolveType(classOf[UriTemplate]).extract(container.resolveType(classOf[Request]).path))
    container.add(classOf[HeaderParameters], () => container.resolveType(classOf[Request]).headers)
    container.add(classOf[QueryParameters], () => container.resolveType(classOf[Request]).query)
    container.add(classOf[FormParameters], () => container.resolveType(classOf[Request]).form)
    container.add(classOf[InputStream], () => container.resolveType(classOf[Request]).input)
    container
  }
}