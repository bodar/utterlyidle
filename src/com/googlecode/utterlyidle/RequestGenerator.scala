package org.webfabric.rest

import java.io.{ByteArrayInputStream, InputStream}
import javax.ws.rs.{HeaderParam, QueryParam, FormParam, PathParam}
import java.lang.reflect.Method

class RequestGenerator(uriTemplate: UriTemplate, method: Method) {
  def generate(arguments: Array[Object]): Request = {
    val pathParams = PathParameters()
    val headers = HeaderParameters()
    val formParams = FormParameters()
    val queryParams = QueryParameters()
    var input: InputStream = new ByteArrayInputStream(new Array[Byte](0))

    arguments.zip(method.getParameterAnnotations).map(pair => {
      val value = pair._1
      val annotations = pair._2

      if (annotations.length > 0) annotations(0) match {
        case path: PathParam => pathParams.add(path.value, value.toString)
        case form: FormParam => formParams.add(form.value, value.toString)
        case query: QueryParam => queryParams.add(query.value, value.toString)
        case header: HeaderParam => headers.add(header.value, value.toString)
        case _ =>
      }
    })
    Request(null, uriTemplate.generate(pathParams), headers, queryParams, formParams, input)
  }
}