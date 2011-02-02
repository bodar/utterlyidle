package com.googlecode.utterlyidle.proxy;

import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.*;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.proxy.Resource.*;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RedirectToTest {
       @Test
    public void canPostRedirectGet() throws Exception {
        TestApplication application = new TestApplication();
        application.add(PostRedirectGet.class);
        Response response = application.handle(post("path/bob"));
        assertThat(response.status(), Matchers.is(SEE_OTHER));
        assertThat(response.header(LOCATION), Matchers.is(Matchers.<Object>notNullValue()));
        assertThat(application.responseAsString(get(response.header(LOCATION))), Matchers.is("bob"));
    }

    @Path("path/{id}")
    public static class PostRedirectGet {
        @POST
        public Response post(@PathParam("id") final String id) {
            return new RedirectTo<PostRedirectGet>(){{ call.get(id); }}.response();
        }

        @GET
        public String get(@PathParam("id") String id) {
            return id;
        }
    }


    @Test
    public void supportsSome() throws Exception {
        RedirectTo redirect = new RedirectTo<SomeResource>() {{ call.getHtml(Option.some("foo")); }};
        assertThat(redirect.location(), is("path/foo"));
    }

    @Test
    public void supportsNone() throws Exception {
        RedirectTo redirect = new RedirectTo<SomeResource>() {{ call.getOptional("id", Option.none(String.class)); }};
        assertThat(redirect.location(), is("path/id"));
    }

    @Test
    public void supportsAlternativeStyle() throws Exception {
        RedirectTo redirect = new RedirectTo<SomeResource>() {{ call.getHtml("foo"); }};
        assertThat(redirect.location(), is("path/foo"));
    }

    @Test
    public void supportsQueryParameters() throws Exception {
        RedirectTo redirect = new RedirectTo<SomeResource>() {{ call.getHtmlWithQuery("foo", "bar"); }};
        assertThat(redirect.location(), is("path/foo?foo=bar"));
    }

    @Test
    public void supportsConvenienceMethodToGetLocation() throws Exception {
        String location = urlOf(resource(SomeResource.class).getHtml("foo"));
        assertThat(location, is("path/foo"));
    }

    @Test
    public void supportsPathParamtersWithThreadLocalVersion() throws Exception {
        assertThat(urlOf(resource(SomeResource.class).getHtml("foo")), is("path/foo"));
    }

    @Test
      public void canExtractPath() {
          assertThat(urlOf(resource(SomeResource.class).getHtml("foo")), Matchers.is("path/foo"));
      }

      @Test
      public void canExtractPathWithStreamingOutput() throws IOException {
          assertThat(urlOf(resource(SomeResource.class).getStreamingHtml("foo")), Matchers.is("path/foo"));
      }

      @Test
      public void canExtractPathWithStreamingWriter() {
          assertThat(urlOf(resource(SomeResource.class).getStreamingWriter("foo")), Matchers.is("path/foo"));
      }

      @Test
      public void canHandleClassWithNoDefaultConstructor() throws IOException {
          assertThat(urlOf(resource(NoDefaultConstructor.class).getStreamingHtml("foo")), Matchers.is("path/foo"));
      }

      @Test
      public void canHandleCustomTypeWithSimpleToString() {
          Id id = Id.id("foo");
          assertThat(urlOf(resource(CustomType.class).getHtml(id)), Matchers.is("path/" + id.toString()));
      }


}
