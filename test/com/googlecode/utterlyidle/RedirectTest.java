package com.googlecode.utterlyidle;

import org.hamcrest.CoreMatchers.*;
import org.junit.Assert.*;
import org.junit.*;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import com.googlecode.utterlyidle.Redirect.resource;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.Status

class RedirectTest{
  @Test
  public void canExtractPath(){
    assertThat(Redirect(resource(SomeResource.class).getHtml("foo")).location, is("path/foo"));
  }

  @Test
  public void canExtractPathWithStreamingOutput(){
    assertThat(Redirect(resource(SomeResource.class).getStreamingHtml("foo")).location, is("path/foo"));
  }


  @Test
  public void canExtractPathWithStreamingWriter(){
    assertThat(Redirect(resource(SomeResource.class).getStreamingWriter("foo")).location, is("path/foo"));
  }

  @Test
  public void canHandleClassWithNoDefaultConstructor(){
    assertThat(Redirect(resource(NoDefaultConstructor.class).getStreamingHtml("foo")).location, is("path/foo"));
  }

  @Test
  public void canHandleCustomTypeWithSimpleToString(){
    Id id = id();
    assertThat(Redirect(resource(CustomType.class).getHtml(id)).location, is("path/" + id.toString))
  }

  @Test
  public void canApplyToResponse(){
    Response response = Response();
    BasePath base = BasePath("");
    Redirect("foo").applyTo(base, response);
    assertThat(response.headers.getValue(HttpHeaders.LOCATION), is("/foo"));
    assertThat(response.code, is(Status.SEE_OTHER));
  }

  @Path("path/{id}")
  static class SomeResource{
    public String getHtml(@PathParam("id") String id) {
        return "bob";
    }

    public StreamingOutput getStreamingHtml(@PathParam("id") String id) {
        return null;
    }

    public StreamingWriter getStreamingWriter(@PathParam("id") String id) {
        return null;
    }
  }

  @Path("path/{id}")
  static class NoDefaultConstructor{
      NoDefaultConstructor(SomeResource someResource) {}

      public String getHtml(@PathParam("id") String id) {
          return "bob";
      }

    public StreamingOutput getStreamingHtml(@PathParam("id") String id){
        return null;
    }
  }

  @Path("path/{id}")
  static class CustomType{
    public void getHtml(@PathParam("id") Id id): String = "bob"
  }
}