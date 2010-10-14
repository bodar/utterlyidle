package org.webfabric.rest

import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit._
import javax.ws.rs.{PathParam, Path}
import org.webfabric.rest.Redirect.resource
import javax.ws.rs.core.{HttpHeaders, StreamingOutput}
import org.webfabric.servlet.{BasePath, ContextPath}
import javax.ws.rs.core.Response.Status
import org.webfabric.properties.Id
import org.webfabric.rest.RedirectTest.{CustomType, NoDefaultConstructor, SomeResource}

class RedirectTest{
  @Test
  def canExtractPath{
    assertThat(Redirect(resource(classOf[SomeResource]).getHtml("foo")).location, is("path/foo"))
  }

  @Test
  def canExtractPathWithStreamingOutput{
    assertThat(Redirect(resource(classOf[SomeResource]).getStreamingHtml("foo")).location, is("path/foo"))
  }


  @Test
  def canExtractPathWithStreamingWriter{
    assertThat(Redirect(resource(classOf[SomeResource]).getStreamingWriter("foo")).location, is("path/foo"))
  }

  @Test
  def canHandleClassWithNoDefaultConstructor{
    assertThat(Redirect(resource(classOf[NoDefaultConstructor]).getStreamingHtml("foo")).location, is("path/foo"))
  }

  @Test
  def canHandleCustomTypeWithSimpleToString{
    val id = Id()
    assertThat(Redirect(resource(classOf[CustomType]).getHtml(id)).location, is("path/" + id.toString))
  }

  @Test
  def canApplyToResponse{
    val response = Response()
    val base = BasePath("")
    Redirect("foo").applyTo(base, response)
    assertThat(response.headers.getValue(HttpHeaders.LOCATION), is("/foo"))
    assertThat(response.code, is(Status.SEE_OTHER))
  }
}

object RedirectTest{

  @Path("path/{id}")
  class SomeResource{
    def getHtml(@PathParam("id") id: String): String = "bob"

    def getStreamingHtml(@PathParam("id") id: String): StreamingOutput = null

    def getStreamingWriter(@PathParam("id") id: String): StreamingWriter = null
  }

  @Path("path/{id}")
  class NoDefaultConstructor(dependancy:SomeResource){
    def getHtml(@PathParam("id") id: String): String = "bob"

    def getStreamingHtml(@PathParam("id") id: String): StreamingOutput = null
  }

  @Path("path/{id}")
  class CustomType{
    def getHtml(@PathParam("id") id: Id): String = "bob"
  }
}