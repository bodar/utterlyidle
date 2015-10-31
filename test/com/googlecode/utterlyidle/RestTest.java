package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.predicates.Predicates;
import com.googlecode.utterlyidle.annotations.Consumes;
import com.googlecode.utterlyidle.annotations.CookieParam;
import com.googlecode.utterlyidle.annotations.DELETE;
import com.googlecode.utterlyidle.annotations.DefaultValue;
import com.googlecode.utterlyidle.annotations.FormParam;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.HEAD;
import com.googlecode.utterlyidle.annotations.PATCH;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.PUT;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Priority;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.annotations.QueryParam;
import com.googlecode.utterlyidle.annotations.View;
import com.googlecode.utterlyidle.examples.HelloWorld;
import com.googlecode.utterlyidle.modules.ArgumentScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.generics.TypeFor;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Formatter;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.predicates.Predicates.instanceOf;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.Entities.streamingOutputOf;
import static com.googlecode.utterlyidle.Entities.streamingWriterOf;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.Request.Builder.accept;
import static com.googlecode.utterlyidle.Request.Builder.contentType;
import static com.googlecode.utterlyidle.Request.delete;
import static com.googlecode.utterlyidle.Request.Builder.form;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.Request.head;
import static com.googlecode.utterlyidle.Request.patch;
import static com.googlecode.utterlyidle.Request.post;
import static com.googlecode.utterlyidle.Request.put;
import static com.googlecode.utterlyidle.Request.Builder.query;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.FORBIDDEN;
import static com.googlecode.utterlyidle.Status.NO_CONTENT;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.annotations.Priority.High;
import static com.googlecode.utterlyidle.annotations.Priority.Low;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;
import static com.googlecode.utterlyidle.io.Converter.asString;
import static com.googlecode.utterlyidle.modules.Modules.requestInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public class RestTest {
    @Test
    public void canDecorateARenderer() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetReturningMyCustomClass.class);
        final Type renderer = new TypeFor<Renderer<MyCustomClass>>() {}.get();
        application.add((RequestScopedModule) container -> {
            container.addType(renderer, MyCustomClassRenderer.class);
            container.decorateType(renderer, MyCustomClassRendererDecorator.class);
            return container;
        });
        application.addResponseHandler(where(entity(), Predicates.is(instanceOf(MyCustomClass.class))), renderer(renderer));
        assertThat(application.responseAsString(Request.get("path")), is("foobar"));

    }

    @Test
    public void supportViews() throws Exception {
        ApplicationBuilder application = application().addAnnotated(ViewResource.class);
        assertThat(application.responseAsString(Request.get("convention")), is("convention"));
        assertThat(application.responseAsString(Request.get("ignored")), is("explicit"));
    }

    @Test
    public void supportMatchedResource() throws Exception {
        ApplicationBuilder application = application().addAnnotated(DependsOnMatchedResource.class);
        assertThat(application.responseAsString(Request.get("hello")), is("DependsOnMatchedResource"));
    }

    @Test
    public void supportUUIDConversion() throws Exception {
        ApplicationBuilder application = application().addAnnotated(UsesUUID.class);
        String uuid = UUID.randomUUID().toString();
        assertThat(application.responseAsString(Request.get("hello", query("name", uuid))), is(uuid));
    }

    @Test
    public void supportDefaultValue() throws Exception {
        ApplicationBuilder application = application().addAnnotated(UsesDefaultValue.class);
        assertThat(application.responseAsString(Request.get("hello", query("name", "Matt"))), is("Matt"));
        assertThat(application.responseAsString(Request.get("hello")), is("Dan"));
    }

    @Test
    public void supportCustomArgumentActivation() throws Exception {
        final String SOME_CUSTOM_VALUE = "some custom value";

        ApplicationBuilder application = application().addAnnotated(UsesCustomValue.class).
                add((ArgumentScopedModule) container ->
                        container.addInstance(CustomValueWithoutPublicConstructor.class, new CustomValueWithoutPublicConstructor(SOME_CUSTOM_VALUE)));
        assertThat(application.responseAsString(Request.get("path")), is(SOME_CUSTOM_VALUE));
    }

    @Test
    public void supportCustomArgumentActivationWithOption() throws Exception {
        ApplicationBuilder application = application().addAnnotated(UsesCustomValueWithOption.class).
                add((ArgumentScopedModule) container ->
                        container.addActivator(CustomValueWithoutPublicConstructor.class, CustomValueWithoutPublicConstructorActivator.class));
        assertThat(application.responseAsString(Request.get("path")), is("true"));
    }

    @Test
    public void whenReturningAResponseUseTheProducesContentTypeIfNoneExplicitlySet() throws Exception {
        ApplicationBuilder application = application().addAnnotated(ReturnsResponseWithContentType.class);
        assertThat(header(application.handle(Request.get("path", query("override", String.valueOf(false)))), CONTENT_TYPE), startsWith(MediaType.APPLICATION_ATOM_XML));
        assertThat(header(application.handle(Request.get("path", query("override", String.valueOf(true)))), CONTENT_TYPE), startsWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void supportReturningResponse() throws Exception {
        assertThat(application().addAnnotated(ReturnsResponse.class).handle(Request.get("path")).status(), is(SEE_OTHER));
    }

    @Test
    public void supportReturningEither() throws Exception {
        assertThat(application().addAnnotated(ReturnsEither.class).responseAsString(Request.get("path")), is("Hello"));
    }

    @Test
    public void canHandleCookies() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GettableWithCookies.class);
        Response response = application.handle(Request.get("foo", HttpMessage.Builder.header("cookie", "name=value")));
        assertThat(response.entity().toString(), is("value"));
        assertThat(header(response, "Set-Cookie"), is("anotherName=\"anotherValue\"; "));
    }

    @Test
    public void canHandleQuotedCookies() throws Exception {
        assertThat(application().addAnnotated(GettableWithCookies.class).responseAsString(Request.get("foo", HttpMessage.Builder.header("cookie", "name=\"value\""))), is("value"));
    }

    @Test
    public void canHandleCookieParams() throws Exception {
        assertThat(application().addAnnotated(GettableWithCookies.class).responseAsString(Request.get("bar", HttpMessage.Builder.header("cookie", "name=bob"))), is("bob"));
    }

    @Test
    public void canGet() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Gettable.class);
        assertThat(application.responseAsString(Request.get("foo")), is("bar"));
    }

    @Test
    public void supportsGetWithListOfUUIDs() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithListOfUUIDs.class);
        Sequence<UUID> expectedIds = Sequences.sequence(UUID.randomUUID(), UUID.randomUUID());
        assertThat(application.responseAsString(Request.get("/path", query("id", expectedIds.first().toString()), query("id", expectedIds.second().toString()))), is("ids"));
    }

    @Test
    public void correctlyErrorsWhenBadlyFormedParameterUsed() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithListOfUUIDs.class);
        Sequence<UUID> ids = Sequences.sequence(UUID.randomUUID(), UUID.randomUUID());
        Response response = application.handle(Request.get("/path", query("id", ids.first() + "," + ids.second())));
        assertThat(response.status(), Matchers.not(OK));
    }

    @Test
    public void leadingSlashInPathShouldNotChangeMatch() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Gettable.class);
        assertThat(application.responseAsString(Request.get("/foo")), is("bar"));
    }

    @Test
    public void canGetWithQueryParameter() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GettableWithQuery.class);
        assertThat(application.responseAsString(Request.get("foo", query("name", "value"))), is("value"));
    }

    @Test
    public void canPostWithFormParameter() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Postable.class);
        assertThat(application.responseAsString(Request.post("foo", form("name", "value"))), is("value"));
    }

    @Test
    public void canHandlePathsOnMethodAsWellAsClass() throws Exception {
        ApplicationBuilder application = application().addAnnotated(MultiplePaths.class);
        assertThat(application.responseAsString(Request.get("foo/bar")), is("found"));
    }

    @Test
    public void canDetermineMethodWhenThereIsAChoice() throws Exception {
        ApplicationBuilder application = application().addAnnotated(MultipleGets.class);
        assertThat(application.responseAsString(Request.get("foo")), is("no parameters"));
        assertThat(application.responseAsString(Request.get("foo", query("arg", "match"))), is("match"));
    }

    @Test
    public void whenThereIsAChoiceOfMatchingMethodsTakesPriorityIntoConsideration() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PrioritisedGets.class);
        assertThat(application.responseAsString(Request.get("foo")), is("highPriority"));
    }

    @Test
    public void canDetermineGetMethodBasedOnMimeType() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithMimeTypes.class);
        assertThat(application.responseAsString(Request.get("text", accept("text/plain"))), is("plain"));
        assertThat(application.responseAsString(Request.get("text", accept("text/html"))), is("html"));
    }

    @Test
    public void setsResponseMimeType() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithMimeTypes.class);

        Response response = application.handle(Request.get("text", accept("text/plain")));
        assertThat(header(response, HttpHeaders.CONTENT_TYPE), startsWith("text/plain"));
    }

    @Test
    public void setsResponseMimeTypeWhenThereAreMultiplePossibleTypes() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithMultipleMimeTypes.class);

        Response plainResponse = application.handle(Request.get("text", accept("text/plain")));
        assertThat(header(plainResponse, HttpHeaders.CONTENT_TYPE), startsWith("text/plain"));
        assertThat(plainResponse.entity().toString(), is("<xml/>"));

        Response xmlResponse = application.handle(Request.get("text", accept("text/xml")));
        assertThat(header(xmlResponse, HttpHeaders.CONTENT_TYPE), startsWith("text/xml"));
        assertThat(xmlResponse.entity().toString(), is("<xml/>"));
    }

    @Test
    public void setsContentTypeCorrectlyEvenWhenNoAcceptHeaderIsPresent() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithSingleMime.class);

        Response plainResponse = application.handle(Request.get("text"));
        assertThat(header(plainResponse, HttpHeaders.CONTENT_TYPE), startsWith("text/plain"));
        assertThat(plainResponse.entity().toString(), is("Hello"));
    }

    @Test
    public void aSingleResourceMethodCanAcceptsMultiplePossibleMimeTypes() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PutWithMultipleMimeTypes.class);

        Response plainResponse = application.handle(Request.put("text", HttpMessage.Builder.header(CONTENT_TYPE, "text/plain"), HttpMessage.Builder.entity("<xml/>")));
        assertThat(plainResponse.status(), is(NO_CONTENT));

        Response xmlResponse = application.handle(Request.put("text", HttpMessage.Builder.header(CONTENT_TYPE, "text/xml"), HttpMessage.Builder.entity("<xml/>")));
        assertThat(xmlResponse.status(), is(NO_CONTENT));
    }

    @Test
    public void canHandleRealWorldAcceptsHeader() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetsWithMimeTypes.class).addAnnotated(PutContent.class);
        String mimeTypes = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
        assertThat(application.responseAsString(Request.get("text", accept(mimeTypes))), is("xml"));

        mimeTypes = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        assertThat(application.responseAsString(Request.put("path/foo", accept(mimeTypes), HttpMessage.Builder.entity("input"))), is("input"));
    }

    @Test
    public void canEcho() throws Exception {
        ApplicationBuilder application = application().addAnnotated(HelloWorld.class);
        assertThat(application.responseAsString(Request.put("echo", HttpMessage.Builder.entity(Entities.inputStreamOf("Hello")))), is("Hello"));
    }

    @Test
    public void canStreamOutput() throws Exception {
        ApplicationBuilder application = application().addAnnotated(StreamOutput.class);
        assertThat(application.responseAsString(Request.get("foo")), is("stream"));
    }

    @Test
    public void canHandleStreamingWriter() throws Exception {
        ApplicationBuilder application = application().addAnnotated(StreamWriter.class);
        assertThat(application.responseAsString(Request.get("foo")), is("writer"));
    }

    @Test
    public void supportsNoContent() throws Exception {
        ApplicationBuilder application = application().addAnnotated(NoContent.class);
        Response response = application.handle(Request.post("foo"));
        assertThat(response.status(), is(NO_CONTENT));
    }

    @Test
    public void ifResourceSetsStatusCodeButNoContentHonorResource() throws Exception {
        ApplicationBuilder application = application().addAnnotated(NoContent.class);
        Response response = application.handle(Request.post("noContentButResponseStatus"));
        assertThat(response.status(), is(OK));
    }

    @Test
    public void doesNotReturnNoContentForHeadRequests() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Headable.class);
        Response response = application.handle(Request.head("foo"));
        assertThat(response.status(), is(OK));
    }

    @Test
    public void supportsPathParameter() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PathParameter.class);
        assertThat(application.responseAsString(Request.get("path/bar")), is("bar"));
    }

    @Test
    public void supportsPathParameterWithSpaces() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PathParameter.class);
        assertThat(application.responseAsString(Request.get("path/bar%20test")), is("bar test"));
        assertThat(application.responseAsString(Request.get("path/bar+test")), is("bar test"));
    }

    @Test
    public void supportsDelete() throws Exception {
        ApplicationBuilder application = application().addAnnotated(DeleteContent.class);
        Response response = application.handle(Request.delete("path/bar"));
        assertThat(response.status(), is(NO_CONTENT));
    }

    @Test
    public void supportsPut() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PutContent.class);

        assertThat(application.responseAsString(Request.put("path/bar", HttpMessage.Builder.entity("input"))), is("input"));
    }

    @Test
    public void supportsPatch() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PatchContent.class);

        assertThat(application.responseAsString(Request.patch("path/bar", HttpMessage.Builder.entity("input"))), is("input"));
    }

    @Test
    public void canDetermineInputHandlerByMimeType() throws Exception {
        ApplicationBuilder application = application().addAnnotated(MultiplePutContent.class);

        assertThat(application.responseAsString(Request.put("text", contentType("text/plain"))), is("plain"));
        assertThat(application.responseAsString(Request.put("text", contentType("text/html"))), is("html"));
    }

    @Test
    public void canPostRedirectGet() throws Exception {
        ApplicationBuilder application = application().addAnnotated(PostRedirectGet.class);
        Response response = application.handle(Request.post("path/bob"));
        assertThat(response.status(), is(SEE_OTHER));
        assertThat(header(response, LOCATION), is(Matchers.<Object>notNullValue()));
        assertThat(application.responseAsString(Request.get(header(response, LOCATION))), is("bob"));
    }

    @Test
    public void canRedirectWithBasePath() throws Exception {
        ApplicationBuilder application = application().addAnnotated(RedirectGet.class).add(requestInstance(BasePath.basePath("base")));
        Response response = application.handle(Request.get("/base/foo/bar"));
        assertThat(header(response, LOCATION), is("/base/foo/baz"));
    }

    @Test
    public void canRedirectWithBaseUri() throws Exception {
        ApplicationBuilder application = application().addAnnotated(BaseUriResource.class);
        Response response = application.handle(Request.get("/foo/bar", HttpMessage.Builder.header(HttpHeaders.HOST, "localhost:8080")));
        assertThat(header(response, LOCATION), is("http://localhost:8080/baz"));
    }

    @Test
    public void canCoerceTypes() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithStrongType.class);
        assertThat(application.responseAsString(Request.get("path/4d237b0a-535f-49e9-86ca-10d28aa3e4f8")), is("4d237b0a-535f-49e9-86ca-10d28aa3e4f8"));
    }

    @Test
    public void canCoerceInvalidEithers() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithEither.class);
        assertThat(application.responseAsString(Request.get("path", query("layout", "invalidValue"))), is("layout:left(invalidValue)"));
    }

    @Test
    public void canCoerceValidEithers() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithEither.class);
        final String value = Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT.toString();
        assertThat(application.responseAsString(Request.get("path", query("layout", value))), is("layout:right(" + value + ")"));
    }


    @Test
    public void canCoerceEithersThatContainAValidOption() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalEither.class);
        final String value = Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT.toString();
        assertThat(application.responseAsString(Request.get("path", query("optionalLayout", value))), is("optionalLayout:right(some(" + value + "))"));
    }

    @Test
    public void canCoerceEithersThatContainAnInvalidOption() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalEither.class);
        final String value = "rubbish";
        assertThat(application.responseAsString(Request.get("path", query("optionalLayout", value))), is("optionalLayout:left(" + value + ")"));
    }

    @Test
    public void canCoerceEithersThatContainAnNone() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalEither.class);
        assertThat(application.responseAsString(Request.get("path")), is("optionalLayout:right(none())"));
    }

    @Test
    public void canCoerceEithersThatContainNone() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalEither.class);
        assertThat(application.responseAsString(Request.get("path")), is("optionalLayout:right(none())"));
    }

    @Test
    public void canCoerceOptionalTypes() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalStrongTypeWithFactoryMethod.class);
        assertThat(application.responseAsString(Request.get("path", query("id", "4d237b0a-535f-49e9-86ca-10d28aa3e4f8"))), is("4d237b0a-535f-49e9-86ca-10d28aa3e4f8"));
    }

    @Test
    public void canCoerceOptionalTypesEvenWhenNoValueIsPresent() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalStrongTypeWithFactoryMethod.class);
        assertThat(application.responseAsString(Request.get("path/")), is("default"));
    }

    @Test
    public void canCoerceOptionalTypesEvenWhenNoValueIsPresentForATypeWithConstructor() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalStrongTypeWithConstructor.class);
        assertThat(application.responseAsString(Request.get("path")), is("default"));
    }

    @Test
    public void canCoerceOptionalStringEvenWhenNoValueIsPresent() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithOptionalString.class);
        assertThat(application.responseAsString(Request.get("path")), is("some default"));
    }

    @Test
    public void supportsCustomRenderer() throws Exception {
        final boolean[] called = {false};
        ApplicationBuilder application = application().addAnnotated(GetReturningMyCustomClass.class);
        application.add((RequestScopedModule) container -> {
            return container.addActivator(MyCustomClassRenderer.class, new Callable<MyCustomClassRenderer>() {
                @Override
                public MyCustomClassRenderer call() throws Exception {
                    called[0] = true;
                    return new MyCustomClassRenderer();
                }
            });
        });
        application.addResponseHandler(where(entity(), Predicates.is(instanceOf(MyCustomClass.class))), renderer(MyCustomClassRenderer.class));
        assertThat(application.responseAsString(Request.get("path")), is("foo"));
        assertThat(called[0], is(true));
    }

    @Test
    public void supportsCustomRendererWithActivator() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetReturningMyCustomClass.class);
        application.addResponseHandler(where(entity(), Predicates.is(instanceOf(MyCustomClass.class))), renderer(MyCustomClassRenderer.class));
        assertThat(application.responseAsString(Request.get("path")), is("foo"));
    }

    @Test
    public void shouldHandleResourceWithAParameterMissingAnAnnotation() throws Exception {
        ApplicationBuilder application = application().addAnnotated(GetWithParameterButNoAnnotation.class);
        assertThat(application.handle(Request.get("path")).status(), is(Status.UNSATISFIABLE_PARAMETERS));
    }

    @Test
    public void shouldSupportHeadIfAlreadyDefined() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Headable.class);
        assertThat(application.handle(Request.head("foo/bar")).status(), is(FORBIDDEN));
    }

    @Test
    public void shouldSupportHeadIfNotAlreadyDefined() throws Exception {
        ApplicationBuilder application = application().addAnnotated(Gettable.class);
        assertThat(application.handle(Request.head("foo")).status(), is(OK));
    }

    @Path("foo")
    public static class Headable {
        @HEAD
        public Response head() { return ResponseBuilder.response(OK).header(CONTENT_LENGTH, "569").build(); }

        @HEAD
        @Path("bar")
        public Response headForbidden() { return ResponseBuilder.response(FORBIDDEN).build(); }

        @GET
        public Response get() {
            throw new RuntimeException("GET should not have been called");
        }
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
            return ResponseBuilder.response().cookie(cookie("anotherName", "anotherValue")).entity(name).build();
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
    public static class MultiplePaths {
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
            return streamingOutputOf("stream");
        }
    }

    @Path("foo")
    public static class StreamWriter {
        @GET
        public StreamingWriter get() {
            return streamingWriterOf("writer");
        }
    }

    public static class NoContent {
        public int count = 0;

        @POST
        @Path("foo")
        public void noContent() {
            count = count + 1;
        }

        @POST
        @Path("noContentButResponseStatus")
        public Response post() {
            return Responses.response(Status.OK);
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
    public static class PatchContent {
        @PATCH
        public String patch(@PathParam("id") String id, InputStream input) {
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
        private final Redirector redirector;

        public PostRedirectGet(Redirector redirector) {
            this.redirector = redirector;
        }

        @POST
        public Response post(@PathParam("id") String id) {
            return redirector.seeOther(method(on(PostRedirectGet.class).get(id)));
        }

        @GET
        public String get(@PathParam("id") String id) {
            return id;
        }
    }

    @Path("foo")
    public static class RedirectGet {
        private final Redirector redirector;

        public RedirectGet(Redirector redirector) {
            this.redirector = redirector;
        }

        @GET
        @Path("bar")
        public Response redirectSource() {
            return redirector.seeOther(method(on(RedirectGet.class).get()));
        }

        @GET
        @Path("baz")
        public String get() {
            return "redirected";
        }

    }

    @Path("foo")
    public static class BaseUriResource {

        private final BaseUri baseUri;

        public BaseUriResource(BaseUri baseUri) {
            this.baseUri = baseUri;
        }

        @GET
        @Path("bar")
        public Response redirect() {
            return ResponseBuilder.response(Status.SEE_OTHER).header(HttpHeaders.LOCATION, baseUri.value().mergePath("baz")).build();
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
    public static class GetWithListOfUUIDs {
        @GET
        public String get(@QueryParam("id") Iterable<UUID> ids) {
            return "ids";
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

    public static class MyCustomClassRendererDecorator implements Renderer<MyCustomClass> {
        private final Renderer<MyCustomClass> renderer;

        public MyCustomClassRendererDecorator(Renderer<MyCustomClass> renderer) {
            this.renderer = renderer;
        }

        public String render(MyCustomClass value) throws Exception {
            return renderer.render(value) + "bar";
        }
    }

    @Path("path")
    public static class ReturnsResponseWithContentType {
        @GET
        @Produces(MediaType.APPLICATION_ATOM_XML)
        public Response get(@QueryParam("override") Boolean override) {
            Response response = response(Status.SEE_OTHER);
            if (override) {
                return modify(response).contentType(MediaType.APPLICATION_JSON).build();
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
        public String get(CustomValueWithoutPublicConstructor value) {
            return value.getValue();
        }
    }

    @Path("path")
    public static class UsesCustomValueWithOption {
        @GET
        public boolean get(@QueryParam("optionalParam") Option<CustomValueWithoutPublicConstructor> option) {
            return option.isEmpty();
        }
    }

    public static class CustomValueWithoutPublicConstructor {
        private final String value;

        CustomValueWithoutPublicConstructor(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class CustomValueWithoutPublicConstructorActivator implements Callable<CustomValueWithoutPublicConstructor> {

        private String value;

        public CustomValueWithoutPublicConstructorActivator(String value) {
            this.value = value;
        }

        public CustomValueWithoutPublicConstructor call() throws Exception {
            return new CustomValueWithoutPublicConstructor(value);
        }
    }

    @Path("hello")
    public static class UsesDefaultValue {
        @GET
        public String get(@QueryParam("name") @DefaultValue("Dan") String name) {
            return name;
        }
    }

    @Path("hello")
    public static class UsesUUID {
        @GET
        public UUID get(@QueryParam("name") UUID name) {
            return name;
        }
    }

    @Path("hello")
    public static class DependsOnMatchedResource {
        private final MatchedResource matchedResource;

        public DependsOnMatchedResource(MatchedResource matchedResource) {
            this.matchedResource = matchedResource;
        }

        @GET
        public String get() {
            return matchedResource.forClass().getSimpleName();
        }
    }

    public static class ViewResource {
        private final View viewName;

        public ViewResource(final View viewName) {
            this.viewName = viewName;
        }

        @GET
        @Path("convention")
        public String convention() {
            return viewName.value();
        }

        @GET
        @Path("ignored")
        @View("explicit")
        public String explicit() {
            return viewName.value();
        }
    }

}