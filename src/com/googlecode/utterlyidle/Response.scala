package org.webfabric.rest

import javax.servlet.http.HttpServletResponse
import java.io.{Writer, ByteArrayOutputStream, OutputStreamWriter, OutputStream}
import java.lang.String
import javax.ws.rs.core.Response.Status

class Response(val writer: Writer, val output: OutputStream) {
  def this(output: OutputStream) = this (new OutputStreamWriter(output), output)

  def this() = this (new ByteArrayOutputStream)

  var code = Status.OK
  val headers = HeaderParameters()

  def setCode(value: Status): Unit = code = value

  def setHeader(name: String, value: String): Unit = headers.add(name, value)

  def write(value: String): Response = {
    writer.write(value)
    this
  }

  def flush = {
    writer.flush
    output.flush
  }
}

object Response {
  def apply(response: HttpServletResponse): Response = {
    new Response(response.getOutputStream) {
      override def setHeader(name: String, value: String) = {
        response.setHeader(name, value)
        headers.add(name, value)
      }

      override def setCode(value: Status) = {
        response.setStatus(value.getStatusCode)
        code = value
      }
    }
  }

  def apply(output: OutputStream): Response = new Response(output)

  def apply(): Response = new Response
}