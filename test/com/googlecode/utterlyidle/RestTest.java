package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.cookies.Cookies;
import com.googlecode.utterlyidle.handlers.RenderingResponseHandler;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.Formatter;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.MemoryResponse.response;
import static com.googlecode.utterlyidle.Priority.High;
import static com.googlecode.utterlyidle.Priority.Low;
import static com.googlecode.utterlyidle.RequestBuilder.*;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieName.cookieName;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.io.Converter.asString;
import static com.googlecode.utterlyidle.proxy.Resource.redirect;
import static com.googlecode.utterlyidle.proxy.Resource.resource;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RestTest {

    @Test
    public void canHandleCookies() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GettableWithCookies.class);
        Request request = get("foo").withHeader("cookie", "name=value").build();
        Response response = response();
        application.handle(request, response);
        assertThat(response.output().toString(), is("value"));
        assertThat(response.header("Set-Cookie"), is("anotherName=\"anotherValue\"; "));
    }

    @Test
    public void canGet() throws Exception {
        TestApplication application = new TestApplication();
        application.add(Gettable.class);
        assertThat(application.handle(get("foo")), is("bar"));
    }

    @Test
    public void leadingSlashInPathShouldNotChangeMatch() throws Exception {
        TestApplication application = new TestApplication();
        application.add(Gettable.class);
        assertThat(application.handle(get("/foo")), is("bar"));
    }

    @Test
    public void canGetWithQueryParameter() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GettableWithQuery.class);
        assertThat(application.handle(get("foo").withQuery("name", "value")), is("value"));
    }

    @Test
    public void canPostWithFormParameter() throws Exception {
        TestApplication application = new TestApplication();
        application.add(Postable.class);
        assertThat(application.handle(post("foo").withForm("name", "value")), is("value"));
    }

    @Test
    public void canHandlePathsOnMethodAsWellAsClass() throws Exception {
        TestApplication application = new TestApplication();
        application.add(MutlilplePaths.class);
        assertThat(application.handle(get("foo/bar")), is("found"));
    }

    @Test
    public void canDetermineMethodWhenThereIsAChoice() throws Exception {
        TestApplication application = new TestApplication();
        application.add(MultipleGets.class);
        assertThat(application.handle(get("foo")), is("no parameters"));
        assertThat(application.handle(get("foo").withQuery("arg", "match")), is("match"));
    }

    @Test
    public void whenThereIsAChoiceOfMatchingMethodsTakesPriorityIntoConsideration() throws Exception {
        TestApplication application = new TestApplication();
        application.add(PrioritisedGets.class);
        assertThat(application.handle(get("foo")), is("highPriority"));
    }

    @Test
    public void canDetermineGetMethodBasedOnMimeType() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetsWithMimeTypes.class);
        assertThat(application.handle(get("text").accepting("text/plain")), is("plain"));
        assertThat(application.handle(get("text").accepting("text/html")), is("html"));
    }

    @Test
    public void setsResponseMimeType() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetsWithMimeTypes.class);

        Response response = response();
        application.handle(get("text").accepting("text/plain"), response);
        assertThat(response.header(HttpHeaders.CONTENT_TYPE), is("text/plain"));
    }

    @Test
    public void canHandleRealWorldAcceptsHeader() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetsWithMimeTypes.class);
        String mimeTypes = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
        assertThat(application.handle(get("text").accepting(mimeTypes)), is("xml"));

        application.add(PutContent.class);
        InputStream input = new ByteArrayInputStream("input".getBytes());
        mimeTypes = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        assertThat(application.handle(put("path/foo").accepting(mimeTypes).withInput(input)), is("input"));
    }

    @Test
    public void canStreamOutput() throws Exception {
        TestApplication application = new TestApplication();
        application.add(StreamOutput.class);
        assertThat(application.handle(get("foo")), is("stream"));
    }

    @Test
    public void canHandleStreamingWriter() throws Exception {
        TestApplication application = new TestApplication();
        application.add(StreamWriter.class);
        assertThat(application.handle(get("foo")), is("writer"));
    }

    @Test
    public void supportsNoContent() throws Exception {
        TestApplication application = new TestApplication();
        application.add(NoContent.class);
        MemoryResponse response = new MemoryResponse();
        application.handle(post("foo"), response);
        assertThat(response.status, is(Status.NO_CONTENT));
    }

    @Test
    public void supportsPathParameter() throws Exception {
        TestApplication application = new TestApplication();
        application.add(PathParameter.class);
        assertThat(application.handle(get("path/bar")), is("bar"));
    }

    @Test
    public void supportsDelete() throws Exception {
        TestApplication application = new TestApplication();
        application.add(DeleteContent.class);
        MemoryResponse response = new MemoryResponse();
        application.handle(delete("path/bar"), response);
        assertThat(response.status, is(Status.NO_CONTENT));
    }

    @Test
    public void supportsPut() throws Exception {
        TestApplication application = new TestApplication();
        application.add(PutContent.class);

        InputStream input = new ByteArrayInputStream("input".getBytes());
        assertThat(application.handle(put("path/bar").withInput(input)), is("input"));
    }

    @Test
    public void canDetermineInputHandlerByMimeType() throws Exception {
        TestApplication application = new TestApplication();
        application.add(MultiplePutContent.class);

        assertThat(application.handle(put("text").withHeader(HttpHeaders.CONTENT_TYPE, "text/plain")), is("plain"));
        assertThat(application.handle(put("text").withHeader(HttpHeaders.CONTENT_TYPE, "text/html")), is("html"));
    }

    @Test
    public void canPostRedirectGet() throws Exception {
        TestApplication application = new TestApplication();
        application.add(PostRedirectGet.class);
        MemoryResponse response = new MemoryResponse();
        application.handle(post("path/bob"), response);
        assertThat(application.handle(get(response.header(HttpHeaders.LOCATION))), is("bob"));
    }

    @Test
    public void canCoerceTypes() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithStrongType.class);
        assertThat(application.handle(get("path/4d237b0a-535f-49e9-86ca-10d28aa3e4f8")), is("4d237b0a-535f-49e9-86ca-10d28aa3e4f8"));
    }

    @Test
    public void canCoerceInvalidEithers() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithEither.class);
        assertThat(application.handle(get("path").withQuery("layout", "invalidValue")), is("layout:left(invalidValue)"));
    }

    @Test
    public void canCoerceValidEithers() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithEither.class);
        final String value = Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT.toString();
        assertThat(application.handle(get("path").withQuery("layout", value)), is("layout:right(" + value + ")"));
    }


    @Test
    public void canCoerceEithersThatContainAValidOption() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithOptionalEither.class);
        final String value = Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT.toString();
        assertThat(application.handle(get("path").withQuery("optionalLayout", value)), is("optionalLayout:right(some(" + value + "))"));
    }

    @Test
    public void canCoerceEithersThatContainAnInvalidOption() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithOptionalEither.class);
        final String value = "rubbish";
        assertThat(application.handle(get("path").withQuery("optionalLayout", value)), is("optionalLayout:left(" + value + ")"));
    }

    @Test
    public void canCoerceEithersThatContainAnNone() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithOptionalEither.class);
        assertThat(application.handle(get("path")), is("optionalLayout:right(none())"));
    }

    @Test
    public void canCoerceEithersThatContainNone() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithOptionalEither.class);
        assertThat(application.handle(get("path")), is("optionalLayout:right(none())"));
    }

    @Test
    public void canCoerceOptionalTypes() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithOptionalStrongTypeWithFactoryMethod.class);
        assertThat(application.handle(get("path").withQuery("id", "4d237b0a-535f-49e9-86ca-10d28aa3e4f8")), is("4d237b0a-535f-49e9-86ca-10d28aa3e4f8"));
    }

    @Test
    public void canCoerceOptionalTypesEvenWhenNoValueIsPresent() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithOptionalStrongTypeWithFactoryMethod.class);
        assertThat(application.handle(get("path/")), is("default"));
    }

    @Test
    public void canCoerceOptionalTypesEvenWhenNoValueIsPresentForATypeWithConstructor() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithOptionalStrongTypeWithConstructor.class);
        assertThat(application.handle(get("path")), is("default"));
    }

    @Test
    public void canCoerceOptionalStringEvenWhenNoValueIsPresent() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithOptionalString.class);
        assertThat(application.handle(get("path")), is("some default"));
    }

    @Test
    public void supportsCustomRenderer() throws Exception {
        TestApplication application = new TestApplication();
        application.addResponseHandler(where(entity(), Predicates.is(instanceOf(MyCustomClass.class))), RenderingResponseHandler.renderer(MyCustomClassRenderer.class));
        application.add(GetReturningMyCustomClass.class);
        assertThat(application.handle(get("path")), is("foo"));
    }

    @Test
    public void shouldHandleResourceWithAParameterMissingAnAnnotation() throws Exception {
        TestApplication application = new TestApplication();
        application.add(GetWithParameterButNoAnnotation.class);
        assertThat(application.responseFor(get("path")).status(), is(Status.UNSATISFIABLE_PARAMETERS));
    }


    @Path("foo")
    public static class Gettable {
        @GET
        public String get() {
            return "bar";
        }
    }

    @Path("foo")
    public static class GettableWithQuery {
        @GET
        public String get(@QueryParam("name") String name) {
            return name;
        }
    }

    @Path("foo")
    public static class GettableWithCookies {
        private final Cookies cookies;

        public GettableWithCookies(Cookies cookies) {
            this.cookies = cookies;
        }

        @GET
        public String get() {
            cookies.set(cookie(cookieName("anotherName"), "anotherValue"));
            cookies.commit();
            return cookies.getValue(cookieName("name"));
        }
    }

    @Path("foo")
    public static class Postable {
        @POST
        public String post(@FormParam("name") String name) {
            return name;
        }
    }

    @Path("foo")
    public static class MutlilplePaths {
        @GET
        @Path("bar")
        public String get() {
            return "found";
        }
    }

    @Path("foo")
    public static class MultipleGets {
        @GET
        public String get() {
            return "no parameters";
        }

        @GET
        public String get(@QueryParam("arg") String arg) {
            return arg;
        }
    }

    @Path("foo")
    public static class PrioritisedGets {
        @GET
        public String A() {
            return "defaultPriority";
        }

        @GET
        @Priority(Low)
        public String B() {
            return "lowPriority";
        }

        @GET
        @Priority(High)
        public String C() {
            return "highPriority";
        }


    }

    @Path("text")
    public static class GetsWithMimeTypes {
        @GET
        @Produces("text/plain")
        public String getPlain() {
            return "plain";
        }

        @GET
        @Produces("application/xml")
        public String getXml() {
            return "xml";
        }

        @GET
        @Produces("text/html")
        public String getHtml() {
            return "html";
        }
    }

    @Path("foo")
    public static class StreamOutput {
        @GET
        public StreamingOutput get() {
            return new StreamingOutput() {
                public void write(OutputStream out) throws IOException {
                    Writer streamWriter = new OutputStreamWriter(out);
                    streamWriter.write("stream");
                    streamWriter.flush();
                }
            };
        }
    }

    @Path("foo")
    public static class StreamWriter {
        @GET
        public StreamingWriter get() {
            return new StreamingWriter() {
                public void write(Writer writer) throws IOException {
                    writer.write("writer");
                }
            };
        }
    }

    @Path("foo")
    public static class NoContent {
        public int count = 0;

        @POST
        public void post() {
            count = count + 1;
        }
    }

    @Path("path/{id}")
    public static class DeleteContent {
        public int count = 0;

        @DELETE
        public void delete(@PathParam("id") String id) {
            count = count + 1;
        }
    }

    @Path("path/{id}")
    public static class PutContent {
        @PUT
        public String put(@PathParam("id") String id, InputStream input) {
            return asString(input);
        }
    }

    @Path("path/{id}")
    public static class PathParameter {
        @GET
        public String get(@PathParam("id") String id) {
            return id;
        }
    }

    @Path("text")
    public static class MultiplePutContent {
        @PUT
        @Consumes("text/plain")
        public String putPlain(InputStream input) {
            return "plain";
        }

        @PUT
        @Consumes("text/html")
        public String putHtml(InputStream input) {
            return "html";
        }
    }

    @Path("path/{id}")
    public static class PostRedirectGet {
        @POST
        public Redirect post(@PathParam("id") String id) {
            return redirect(resource(PostRedirectGet.class).get(id));
        }

        @GET
        public String get(@PathParam("id") String id) {
            return id;
        }
    }

    @Path("path/{id}")
    public static class GetWithStrongType {
        @GET
        public String get(@PathParam("id") Id id) {
            return id.toString();
        }
    }

    public static class ClassWithPublicConstructor {

        private final String value;

        public ClassWithPublicConstructor(String value) {
            this.value = value;
        }

        public ClassWithPublicConstructor(Integer value) {
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Path("path")
    public static class GetWithOptionalStrongTypeWithFactoryMethod {
        @GET
        public String get(@QueryParam("id") Option<Id> id) {
            return id.getOrElse(Id.id("default")).toString();
        }
    }

    @Path("path")
    public static class GetWithOptionalStrongTypeWithConstructor {
        @GET
        public String get(@QueryParam("name") Option<ClassWithPublicConstructor> id) {
            return id.getOrElse(new ClassWithPublicConstructor("default")).toString();
        }
    }

    @Path("path")
    public static class GetWithOptionalString {
        @GET
        public String get(@QueryParam("name") Option<String> id) {
            return id.getOrElse("some default");
        }
    }

    @Path("path")
    public static class GetWithEither {
        @GET
        public String get(@QueryParam("layout") Either<String, Formatter.BigDecimalLayoutForm> invalidOrEnum) {
            return "layout:" + invalidOrEnum.toString();
        }
    }

    @Path("path")
    public static class GetWithParameterButNoAnnotation {
        @GET
        public void get(String test) {
            System.out.println(test);
        }

    }

    @Path("path")
    public static class GetWithOptionalEither {
        @GET
        public String getOptional(@QueryParam("optionalLayout") Either<String, Option<Formatter.BigDecimalLayoutForm>> invalidOrEnum) {
            return "optionalLayout:" + invalidOrEnum.toString();
        }
    }

    public static class MyCustomClass {
    }

    @Path("path")
    public static class GetReturningMyCustomClass {
        @GET
        public MyCustomClass get() {
            return new MyCustomClass();
        }
    }

    public static class MyCustomClassRenderer implements Renderer<MyCustomClass> {
        public String render(MyCustomClass value) {
            return "foo";
        }
    }
}