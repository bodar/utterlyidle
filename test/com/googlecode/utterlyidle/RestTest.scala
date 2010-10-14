package org.webfabric.rest

import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit._
import org.webfabric.io.Converter.asString
import org.webfabric.rest.RestTest._
import javax.ws.rs._
import core.Response.Status
import core.{HttpHeaders, StreamingOutput}
import java.io._
import RequestBuilder._
import com.googlecode.yadic.SimpleContainer
import Redirect.resource
import org.webfabric.properties.Id

class RestTest {
  @Test
  def canGet() {
    val engine = new TestEngine
    engine.add(classOf[Gettable])
    assertThat(engine.handle(get("foo")), is("bar"))
  }

  @Test
  def leadingSlashInPathShouldNotChangeMatch() {
    val engine = new TestEngine
    engine.add(classOf[Gettable])
    assertThat(engine.handle(get("/foo")), is("bar"))
  }

  @Test
  def canGetWithQueryParameter() {
    val engine = new TestEngine
    engine.add(classOf[GettableWithQuery])
    assertThat(engine.handle(get("foo").withQuery("name" -> "value")), is("value"))
  }

  @Test
  def canPostWithFormParameter() {
    val engine = new TestEngine
    engine.add(classOf[Postable])
    assertThat(engine.handle(post("foo").withForm("name" -> "value")), is("value"))
    assertThat(engine.handle(post("foo").withHeader(HttpHeaders.CONTENT_TYPE -> "application/x-www-form-urlencoded").withForm("name" -> "value")), is("value"))
  }

  @Test
  def canHandlePathsOnMethodAsWellAsClass() {
    val engine = new TestEngine
    engine.add(classOf[MutlilplePaths])
    assertThat(engine.handle(get("foo/bar")), is("found"))
  }

  @Test
  def canDetermineMethodWhenThereIsAChoice() {
    val engine = new TestEngine
    engine.add(classOf[MutlilpleGets])
    assertThat(engine.handle(get("foo")), is("no parameters"))
    assertThat(engine.handle(get("foo").withQuery("arg" -> "match")), is("match"))
  }

  @Test
  def canDetermineGetMethodBasedOnMimeType() {
    val engine = new TestEngine
    engine.add(classOf[GetsWithMimeTypes])
    assertThat(engine.handle(get("text").accepting("text/plain")), is("plain"))
    assertThat(engine.handle(get("text").accepting("text/html")), is("html"))
  }

  @Test
  def setsResponseMimeType() {
    val engine = new TestEngine
    engine.add(classOf[GetsWithMimeTypes])

    var response = new Response
    engine.handle(get("text").accepting("text/plain"), response)
    assertThat(response.headers.getValue(HttpHeaders.CONTENT_TYPE), is("text/plain"))
  }

  @Test
  def canHandleRealWorldAcceptsHeader() {
    val engine = new TestEngine
    engine.add(classOf[GetsWithMimeTypes])
    var mimeTypes = """application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"""
    assertThat(engine.handle(get("text").accepting(mimeTypes)), is("xml"))

    engine.add(classOf[PutContent])
    val input = new ByteArrayInputStream("input".getBytes)
    mimeTypes = """text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"""
    assertThat(engine.handle(put("path/foo").accepting(mimeTypes).withInput(input)), is("input"))
  }

  @Test
  def canStreamOutput() {
    val engine = new TestEngine
    engine.add(classOf[StreamOutput])
    assertThat(engine.handle(get("foo")), is("stream"))
  }

  @Test
  def canHandleStreamingWriter() {
    val engine = new TestEngine
    engine.add(classOf[StreamWriter])
    assertThat(engine.handle(get("foo")), is("writer"))
  }

  @Test
  def supportsNoContent() {
    val engine = new TestEngine
    engine.add(classOf[NoContent])
    val response = new Response()
    engine.handle(post( "foo"), response)
    assertThat(response.code, is(Status.NO_CONTENT))
  }

  @Test
  def supportsPathParameter() {
    val engine = new TestEngine
    engine.add(classOf[PathParameter])
    assertThat(engine.handle(get( "path/bar")), is("bar"))
  }

  @Test
  def supportsDelete() {
    val engine = new TestEngine
    engine.add(classOf[DeleteContent])
    val response = new Response()
    engine.handle(delete( "path/bar"), response)
    assertThat(response.code, is(Status.NO_CONTENT))
  }

  @Test
  def supportsPut() {
    val engine = new TestEngine
    engine.add(classOf[PutContent])

    val input = new ByteArrayInputStream("input".getBytes)
    assertThat(engine.handle(put("path/bar").withInput(input)), is("input"))
  }

  @Test
  def canDetermineInputHandlerByMimeType() {
    val engine = new TestEngine
    engine.add(classOf[MultiplePutContent])

    assertThat(engine.handle(put("text").withHeader(HttpHeaders.CONTENT_TYPE -> "text/plain")), is("plain"))
    assertThat(engine.handle(put("text").withHeader(HttpHeaders.CONTENT_TYPE -> "text/html")), is("html"))
  }

  @Test
  def canPostRedirectGet() {
    val engine = new TestEngine
    engine.add(classOf[PostRedirectGet])
    val response = Response()
    engine.handle(post("path/bob"), response)
    assertThat(engine.handle(get(response.headers.getValue(HttpHeaders.LOCATION))), is("bob"))
  }

  @Test
  def canCoerceTypes() {
    val engine = new TestEngine
    engine.add(classOf[GetWithStrongType])
    assertThat(engine.handle(get("path/4d237b0a-535f-49e9-86ca-10d28aa3e4f8")), is("4d237b0a-535f-49e9-86ca-10d28aa3e4f8"))
  }
}

object RestTest {
  class TestEngine{
    val engine = new RestEngine
    val container = new SimpleContainer

    def add(resource: Class[_]):TestEngine = {
      engine.add(resource)
      container.add(resource)
      this
    }

    def handle(request:Request):String = {
      val output = new ByteArrayOutputStream
      val response: Response = Response(output)
      handle(request, response)
      response.flush
      output.toString
    }

    def handle(request:Request, response:Response):Unit = {
      engine.handle(container, request, response)
    }
  }


  @Path("foo")
  class Gettable {
    @GET
    def get(): String = {
      "bar"
    }
  }

  @Path("foo")
  class GettableWithQuery {
    @GET
    def get(@QueryParam("name") name: String): String = {
      name
    }
  }

  @Path("foo")
  class Postable {
    @POST
    def post(@FormParam("name") name: String): String = {
      name
    }
  }

  @Path("foo")
  class MutlilplePaths {
    @GET
    @Path("bar")
    def get(): String = {
      "found"
    }
  }

  @Path("foo")
  class MutlilpleGets {
    @GET
    def get(): String = {
      "no parameters"
    }

    @GET
    def get(@QueryParam("arg") arg: String): String = {
      arg
    }
  }

  @Path("text")
  class GetsWithMimeTypes {
    @GET
    @Produces(Array("text/plain"))
    def getPlain(): String = {
      "plain"
    }

    @GET
    @Produces(Array("application/xml"))
    def getXml(): String = {
      "xml"
    }

    @GET
    @Produces(Array("text/html"))
    def getHtml(): String = {
      "html"
    }
  }

  @Path("foo")
  class StreamOutput {
    @GET
    def get(): StreamingOutput = {
      new StreamingOutput {
        def write(out: OutputStream) = {
          var streamWriter = new OutputStreamWriter(out)
          streamWriter.write("stream")
          streamWriter.flush
        }
      }
    }
  }

  @Path("foo")
  class StreamWriter {
    @GET
    def get(): StreamingWriter = {
      new StreamingWriter {
        def write(writer: Writer) = {
          writer.write("writer")
        }
      }
    }
  }

  @Path("foo")
  class NoContent {
    var count = 0
    @POST
    def post(): Unit = {
      count = count + 1
    }
  }

  @Path("path/{id}")
  class DeleteContent {
    var count = 0
    @DELETE
    def delete(@PathParam("id") id:String): Unit = {
      count = count + 1
    }
  }

 @Path("path/{id}")
  class PutContent {
    @PUT
    def put(@PathParam("id") id:String, input:InputStream): String = {
      asString(input)
    }
  }

  @Path("path/{id}")
  class PathParameter {
    @GET
    def get(@PathParam("id") id:String): String = {
      id
    }
  }

  @Path("text")
  class MultiplePutContent {
    @PUT
    @Consumes(Array("text/plain"))
    def putPlain(input:InputStream): String = {
      "plain"
    }

    @PUT
    @Consumes(Array("text/html"))
    def putHtml(input:InputStream): String = {
      "html"
    }
  }

  @Path("path/{id}")
  class PostRedirectGet {
    @POST
    def post(@PathParam("id") id:String): Redirect = Redirect(resource(classOf[PostRedirectGet]).get(id))

    @GET
    def get(@PathParam("id") id:String): String = id
  }

  @Path("path/{id}")
  class GetWithStrongType {
    @GET
    def get(@PathParam("id") id:Id): String = id.value
  }


}