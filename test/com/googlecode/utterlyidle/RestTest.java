package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.annotations.Consumes;
import com.googlecode.utterlyidle.annotations.CookieParam;
import com.googlecode.utterlyidle.annotations.DELETE;
import com.googlecode.utterlyidle.annotations.DefaultValue;
import com.googlecode.utterlyidle.annotations.FormParam;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.PUT;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Priority;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.annotations.QueryParam;
import com.googlecode.utterlyidle.modules.ArgumentScopedModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.yadic.Container;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Formatter;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.RequestBuilder.delete;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.RequestBuilder.put;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.NO_CONTENT;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.annotations.Priority.High;
import static com.googlecode.utterlyidle.annotations.Priority.Low;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;
import static com.googlecode.utterlyidle.io.Converter.asString;
import static com.googlecode.utterlyidle.proxy.Resource.redirect;
import static com.googlecode.utterlyidle.proxy.Resource.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public class RestTest {
    @Test
    public void supportDefaultValue() throws Exception {
        ApplicationBuilder application = application().addAnnotated(UsesDefaultValue.class);
        assertThat(application.responseAsString(get("hello").withQuery("name", "Matt")), is("Matt"));
        assertThat(application.responseAsString(get("hello")), is("Dan"));
    }

    @Test
    public void supportCustomArgumentActivation() throws Exception {
        final String SOME_CUSTOM_VALUE = "some custom value";

        ApplicationBuilder application = application().addAnnotated(UsesCustomValue.class).add(new ArgumentScopedModule() {
            public Module addPerArgumentObjects(Container container) {
                container.addInstance(CustomValueWithoutPublicContructor.class, new CustomValueWithoutPublicContructor(SOME_CUSTOM_VALUE));
                return this;
            }
        });
        assertThat(application.responseAsString(get("path")), is(SOME_CUSTOM_VALUE));
    }

    @Test
    public void supportCustomArgumentActivationWithOption() throws Exception {
        ApplicationBuilder application = application().addAnnotated(UsesCustomValueWithOption.class).add(new ArgumentScopedModule() {
            public Module addPerArgumentObjects(Container container) {
                container.addActivator(CustomValueWithoutPublicContructor.class, CustomValueWithoutPublicContructorActivator.class);
                return this;
            }
        });
        assertThat(application.responseAsString(get("path")), is("true"));
    }

    @Test
    public void whenReturningAResponseUseTheProducesContentTypeIfNoneExplicitlySet() throws Exception {
        ApplicationBuilder application = application().addAnnotated(ReturnsResponseWithContentType.class);
        assertThat(application.handle(get("path").withQuery("override", String.valueOf(false))).header(CONTENT_TYPE), startsWith(MediaType.APPLICATION_ATOM_XML));
        assertThat(application.handle(get("path").withQuery("override", String.valueOf(true))).header(CONTENT_TYPE), startsWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void supportReturningResponse() throws Exception {
        assertThat(application().addAnnotated(ReturnsResponse.class).handle(get("path")).status(), is(SEE_OTHER));
    }

    @Test
    public void supportReturningEither() throws Exception {
        assertThat(application().addAnnotated(ReturnsEither.class).responseAsString(get("path")), is("Hello"));
    }

    @Test
    public void canHandleCookies() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GettableWithCookies.class);
        Response response = application.handle(get("foo").withHeader("cookie", "name=value"));
        assertThat(response.output().toString(), is("value"));
        assertThat(response.header("Set-Cookie"), is("anotherName=\"anotherValue\"; "));
    }

    @Test
    public void canHandleQuotedCookies() throws Exception {
        assertThat(application().addAnnotated(GettableWithCookies.class).responseAsString(get("foo").withHeader("cookie", "name=\"value\"")), is("value"));
    }

    @Test
    public void canHandleCookieParams() throws Exception {
        assertThat(application().addAnnotated(GettableWithCookies.class).responseAsString(get("bar").withHeader("cookie", "name=bob")), is("bob"));
    }

    @Test
    public void canGet() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Gettable.class);
        assertThat(application.responseAsString(get("foo")), is("bar"));
    }

    @Test
    public void leadingSlashInPathShouldNotChangeMatch() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Gettable.class);
        assertThat(application.responseAsString(get("/foo")), is("bar"));
    }

    @Test
    public void canGetWithQueryParameter() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GettableWithQuery.class);
        assertThat(application.responseAsString(get("foo").withQuery("name", "value")), is("value"));
    }

    @Test
    public void canPostWithFormParameter() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Postable.class);
        assertThat(application.responseAsString(post("foo").withForm("name", "value")), is("value"));
    }

    @Test
    public void canHandlePathsOnMethodAsWellAsClass() throws Exception {
        ApplicationBuilder application = application().addAnnotated(MutlilplePaths.class);
        assertThat(application.responseAsString(get("foo/bar")), is("found"));
    }

    @Test
    public void canDetermineMethodWhenThereIsAChoice() throws Exception {
        ApplicationBuilder application = application().addAnnotated(MultipleGets.class);
        assertThat(application.responseAsString(get("foo")), is("no parameters"));
        assertThat(application.responseAsString(get("foo").withQuery("arg", "match")), is("match"));
    }

    @Test
    public void whenThereIsAChoiceOfMatchingMethodsTakesPriorityIntoConsideration() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PrioritisedGets.class);
        assertThat(application.responseAsString(get("foo")), is("highPriority"));
    }

    @Test
    public void canDetermineGetMethodBasedOnMimeType() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithMimeTypes.class);
        assertThat(application.responseAsString(get("text").accepting("text/plain")), is("plain"));
        assertThat(application.responseAsString(get("text").accepting("text/html")), is("html"));
    }

    @Test
    public void setsResponseMimeType() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithMimeTypes.class);

        Response response = application.handle(get("text").accepting("text/plain"));
        assertThat(response.header(HttpHeaders.CONTENT_TYPE), startsWith("text/plain"));
    }

    @Test
    public void setsResponseMimeTypeWhenThereAreMultiplePossibleTypes() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithMultipleMimeTypes.class);

        Response plainResponse = application.handle(get("text").accepting("text/plain"));
        assertThat(plainResponse.header(HttpHeaders.CONTENT_TYPE), startsWith("text/plain"));
        assertThat(new String(plainResponse.bytes()), is("<xml/>"));

        Response xmlResponse = application.handle(get("text").accepting("text/xml"));
        assertThat(xmlResponse.header(HttpHeaders.CONTENT_TYPE), startsWith("text/xml"));
        assertThat(new String(xmlResponse.bytes()), is("<xml/>"));
    }

    @Test
    public void setsContentTypeCorrectlyEvenWhenNoAcceptHeaderIsPresent() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithSingleMime.class);

        Response plainResponse = application.handle(get("text"));
        assertThat(plainResponse.header(HttpHeaders.CONTENT_TYPE), startsWith("text/plain"));
        assertThat(new String(plainResponse.bytes()), is("Hello"));
    }

    @Test
    public void aSingleResouceMethodCanAcceptsMultiplePossibleMimeTypes() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PutWithMultipleMimeTypes.class);

        Response plainResponse = application.handle(put("text").withHeader(CONTENT_TYPE, "text/plain").withInput("<xml/>".getBytes()));
        assertThat(plainResponse.status(), is(NO_CONTENT));

        Response xmlResponse = application.handle(put("text").withHeader(CONTENT_TYPE, "text/xml").withInput("<xml/>".getBytes()));
        assertThat(xmlResponse.status(), is(NO_CONTENT));
    }

    @Test
    public void canHandleRealWorldAcceptsHeader() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithMimeTypes.class);
        String mimeTypes = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
        assertThat(application.responseAsString(get("text").accepting(mimeTypes)), is("xml"));

        application.addAnnotated(PutContent.class);
        mimeTypes = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        assertThat(application.responseAsString(put("path/foo").accepting(mimeTypes).withInput("input".getBytes())), is("input"));
    }

    @Test
    public void canStreamOutput() throws Exception {
        ApplicationBuilder application = application().addAnnotated(StreamOutput.class);
        assertThat(application.responseAsString(get("foo")), is("stream"));
    }

    @Test
    public void canHandleStreamingWriter() throws Exception {
        ApplicationBuilder application = application().addAnnotated(StreamWriter.class);
        assertThat(application.responseAsString(get("foo")), is("writer"));
    }

    @Test
    public void supportsNoContent() throws Exception {
        ApplicationBuilder application = application().addAnnotated(NoContent.class);
        Response response = application.handle(post("foo"));
        assertThat(response.status(), is(Status.NO_CONTENT));
    }

    @Test
    public void supportsPathParameter() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PathParameter.class);
        assertThat(application.responseAsString(get("path/bar")), is("bar"));
    }

    @Test
    public void supportsDelete() throws Exception {
        ApplicationBuilder application = application().addAnnotated(DeleteContent.class);
        Response response = application.handle(delete("path/bar"));
        assertThat(response.status(), is(Status.NO_CONTENT));
    }

    @Test
    public void supportsPut() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PutContent.class);

        assertThat(application.responseAsString(put("path/bar").withInput("input".getBytes())), is("input"));
    }

    @Test
    public void canDetermineInputHandlerByMimeType() throws Exception {
        ApplicationBuilder application = application().addAnnotated(MultiplePutContent.class);

        assertThat(application.responseAsString(put("text").withHeader(HttpHeaders.CONTENT_TYPE, "text/plain")), is("plain"));
        assertThat(application.responseAsString(put("text").withHeader(HttpHeaders.CONTENT_TYPE, "text/html")), is("html"));
    }

    @Test
    public void canPostRedirectGet() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PostRedirectGet.class);
        Response response = application.handle(post("path/bob"));
        assertThat(response.status(), is(SEE_OTHER));
        assertThat(response.header(LOCATION), is(Matchers.<Object>notNullValue()));
        assertThat(application.responseAsString(get(response.header(LOCATION))), is("bob"));
    }

    @Test
    public void canCoerceTypes() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithStrongType.class);
        assertThat(application.responseAsString(get("path/4d237b0a-535f-49e9-86ca-10d28aa3e4f8")), is("4d237b0a-535f-49e9-86ca-10d28aa3e4f8"));
    }

    @Test
    public void canCoerceInvalidEithers() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithEither.class);
        assertThat(application.responseAsString(get("path").withQuery("layout", "invalidValue")), is("layout:left(invalidValue)"));
    }

    @Test
    public void canCoerceValidEithers() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithEither.class);
        final String value = Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT.toString();
        assertThat(application.responseAsString(get("path").withQuery("layout", value)), is("layout:right(" + value + ")"));
    }


    @Test
    public void canCoerceEithersThatContainAValidOption() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalEither.class);
        final String value = Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT.toString();
        assertThat(application.responseAsString(get("path").withQuery("optionalLayout", value)), is("optionalLayout:right(some(" + value + "))"));
    }

    @Test
    public void canCoerceEithersThatContainAnInvalidOption() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalEither.class);
        final String value = "rubbish";
        assertThat(application.responseAsString(get("path").withQuery("optionalLayout", value)), is("optionalLayout:left(" + value + ")"));
    }

    @Test
    public void canCoerceEithersThatContainAnNone() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalEither.class);
        assertThat(application.responseAsString(get("path")), is("optionalLayout:right(none())"));
    }

    @Test
    public void canCoerceEithersThatContainNone() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalEither.class);
        assertThat(application.responseAsString(get("path")), is("optionalLayout:right(none())"));
    }

    @Test
    public void canCoerceOptionalTypes() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalStrongTypeWithFactoryMethod.class);
        assertThat(application.responseAsString(get("path").withQuery("id", "4d237b0a-535f-49e9-86ca-10d28aa3e4f8")), is("4d237b0a-535f-49e9-86ca-10d28aa3e4f8"));
    }

    @Test
    public void canCoerceOptionalTypesEvenWhenNoValueIsPresent() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalStrongTypeWithFactoryMethod.class);
        assertThat(application.responseAsString(get("path/")), is("default"));
    }

    @Test
    public void canCoerceOptionalTypesEvenWhenNoValueIsPresentForATypeWithConstructor() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalStrongTypeWithConstructor.class);
        assertThat(application.responseAsString(get("path")), is("default"));
    }

    @Test
    public void canCoerceOptionalStringEvenWhenNoValueIsPresent() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalString.class);
        assertThat(application.responseAsString(get("path")), is("some default"));
    }

    @Test
    public void supportsCustomRenderer() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetReturningMyCustomClass.class);
        application.addResponseHandler(where(entity(), Predicates.is(instanceOf(MyCustomClass.class))), renderer(MyCustomClassRenderer.class));
        assertThat(application.responseAsString(get("path")), is("foo"));
    }

    @Test
    public void shouldHandleResourceWithAParameterMissingAnAnnotation() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithParameterButNoAnnotation.class);
        assertThat(application.handle(get("path")).status(), is(Status.UNSATISFIABLE_PARAMETERS));
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

    public static class GettableWithCookies {
        @GET
        @Path("foo")
        public Response getAndSet(@CookieParam("name") String name) {
            return response().cookie("anotherName", cookie("anotherValue")).entity(name);
        }

        @GET
        @Path("bar")
        public String get(@CookieParam("name") String name) {
            return name;
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

    @Path("text")
    public static class GetsWithSingleMime {
        @GET
        @Produces("text/plain")
        public String getPlain() {
            return "Hello";
        }

    }

    @Path("text")
    public static class GetsWithMultipleMimeTypes {
        @GET
        @Produces({"text/plain", "text/xml"})
        public String getPlain() {
            return "<xml/>";
        }

    }

    @Path("text")
    public static class PutWithMultipleMimeTypes {
        @PUT
        @Consumes({"text/plain", "text/xml"})
        public void getPlain(InputStream input) {
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
        public Response post(@PathParam("id") String id) {
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

    @Path("path")
    public static class ReturnsResponseWithContentType {
        @GET
        @Produces(MediaType.APPLICATION_ATOM_XML)
        public Response get(@QueryParam("override") Boolean override) {
            Response response = response(Status.SEE_OTHER);
            if (override) {
                response = response.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            }
            return response;
        }
    }

    @Path("path")
    public static class ReturnsResponse {
        @GET
        public Response get() {
            return response(Status.SEE_OTHER);
        }
    }

    @Path("path")
    public static class ReturnsEither {
        @GET
        public Either<String, Exception> get() {
            return left("Hello");
        }
    }

    @Path("path")
    public static class UsesCustomValue {
        @GET
        public String get(CustomValueWithoutPublicContructor value) {
            return value.getValue();
        }
    }

    @Path("path")
    public static class UsesCustomValueWithOption {
        @GET
        public boolean get(@QueryParam("optionalParam") Option<CustomValueWithoutPublicContructor> option) {
            return option.isEmpty();
        }
    }

    public static class CustomValueWithoutPublicContructor {
        private final String value;

        CustomValueWithoutPublicContructor(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class CustomValueWithoutPublicContructorActivator implements Callable<CustomValueWithoutPublicContructor> {

        private String value;

        public CustomValueWithoutPublicContructorActivator(String value) {
            this.value = value;
        }

        public CustomValueWithoutPublicContructor call() throws Exception {
            return new CustomValueWithoutPublicContructor(value);
        }
    }

    @Path("hello")
    public static class UsesDefaultValue {
        @GET
        public String get(@QueryParam("name") @DefaultValue("Dan") String name) {
            return name;
        }
    }
}