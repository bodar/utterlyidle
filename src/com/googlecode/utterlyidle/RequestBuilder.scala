package org.webfabric.rest

import javax.ws.rs.HttpMethod
import java.io.{ByteArrayInputStream, InputStream}
import javax.ws.rs.core.HttpHeaders

class RequestBuilder(method:String, path:String){
  val headers = HeaderParameters()
  val query = QueryParameters()
  val form = FormParameters()
  var input:InputStream = new ByteArrayInputStream(new Array[Byte](0))

  def accepting(value:String) = withHeader(HttpHeaders.ACCEPT, value)

  def withHeader(pair: (String, String)) = {
    headers.add(pair._1, pair._2)
    this
  }

  def withQuery(pair: (String, String)) = {
    query.add(pair._1, pair._2)
    this
  }

  def withForm(pair: (String, String)) = {
    form.add(pair._1, pair._2)
    this
  }

  def withInput(input:InputStream) = {
    this.input = input
    this
  }

  def build:Request = {
    Request(method, path, headers, query, form, input)
  }
}

object RequestBuilder{
  def get(path:String) = new RequestBuilder(HttpMethod.GET, path)
  def post(path:String) = new RequestBuilder(HttpMethod.POST, path)
  def put(path:String) = new RequestBuilder(HttpMethod.PUT, path)
  def delete(path:String) = new RequestBuilder(HttpMethod.DELETE, path)

  implicit def toRequest(builder:RequestBuilder):Request = builder.build
}