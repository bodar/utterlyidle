package com.googlecode.utterlyidle.proxy;

import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.CustomType;
import com.googlecode.utterlyidle.Id;
import com.googlecode.utterlyidle.NoDefaultConstructor;
import com.googlecode.utterlyidle.Redirect;
import com.googlecode.utterlyidle.SomeResource;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;

import static com.googlecode.utterlyidle.proxy.Resource.redirect;
import static com.googlecode.utterlyidle.proxy.Resource.resource;
import static com.googlecode.utterlyidle.proxy.Resource.urlOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RedirectToTest {
    @Test
    public void supportsOptions() throws Exception {
        Redirect redirect = new RedirectTo<SomeResource>() {{ call.getHtml(Option.some("foo")); }};
        assertThat(redirect.location(), is("path/foo"));
    }

    @Test
    public void supportsAlternativeStyle() throws Exception {
        Redirect redirect = new RedirectTo<SomeResource>() {{ call.getHtml("foo"); }};
        assertThat(redirect.location(), is("path/foo"));
    }

    @Test
    public void supportsQueryParameters() throws Exception {
        Redirect redirect = new RedirectTo<SomeResource>() {{ call.getHtmlWithQuery("foo", "bar"); }};
        assertThat(redirect.location(), is("path/foo?foo=bar"));
    }

    @Test
    public void supportsConvenienceMethodToGetLocation() throws Exception {
        String location = urlOf(resource(SomeResource.class).getHtml("foo"));
        assertThat(location, is("path/foo"));
    }

    @Test
    public void supportsPathParamtersWithThreadLocalVersion() throws Exception {
        Redirect redirect = redirect(resource(SomeResource.class).getHtml("foo"));
        assertThat(redirect.location(), is("path/foo"));
    }

    @Test
      public void canExtractPath() {
          assertThat(redirect(resource(SomeResource.class).getHtml("foo")).location(), Matchers.is("path/foo"));
      }

      @Test
      public void canExtractPathWithStreamingOutput() throws IOException {
          assertThat(redirect(resource(SomeResource.class).getStreamingHtml("foo")).location(), Matchers.is("path/foo"));
      }

      @Test
      public void canExtractPathWithStreamingWriter() {
          assertThat(redirect(resource(SomeResource.class).getStreamingWriter("foo")).location(), Matchers.is("path/foo"));
      }

      @Test
      public void canHandleClassWithNoDefaultConstructor() throws IOException {
          assertThat(redirect(resource(NoDefaultConstructor.class).getStreamingHtml("foo")).location(), Matchers.is("path/foo"));
      }

      @Test
      public void canHandleCustomTypeWithSimpleToString() {
          Id id = Id.id("foo");
          assertThat(redirect(resource(CustomType.class).getHtml(id)).location(), Matchers.is("path/" + id.toString()));
      }


}
