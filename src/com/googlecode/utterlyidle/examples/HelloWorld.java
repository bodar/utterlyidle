package com.googlecode.utterlyidle.examples;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.numbers.Numbers;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.annotations.*;

import java.io.InputStream;
import java.util.Date;

import static com.googlecode.totallylazy.functions.Callables.toString;
import static com.googlecode.utterlyidle.Entities.streamingOutputOf;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_PROTO;
import static com.googlecode.utterlyidle.ResponseBuilder.response;

@Produces(MediaType.TEXT_PLAIN)
public class HelloWorld {
    @GET
    @Path("primes")
    public Sequence<Number> primes() {
        return Numbers.primes();
    }

    @GET
    @Path("chunk")
    public StreamingOutput chunk() {
        return streamingOutputOf("chunk");
    }

    @GET
    @Path("cacheable")
    public Response cacheable() {
        return response(Status.OK).header(HttpHeaders.CACHE_CONTROL, "public, max-age=60").entity("cacheable").build();
    }

    @GET
    @Path("slow")
    public String slow() throws InterruptedException {
        Thread.sleep(50);
        return "I took 50ms to run";
    }

    @GET
    @Path("etag")
    public String etag() {
        return "abc";
    }

    @GET
    @Path("helloworld/redirect")
    public Response redirect() {
        return Responses.seeOther("helloworld/queryparam");
    }

    @GET
    @Path("helloworld/inresponseheaders")
    public Response getx(@QueryParam("name") String name) {
        return response(Status.OK).header("greeting", hello(name)).entity("").build();
    }

    @OPTIONS
    @Path("helloworld/options")
    public String options(@QueryParam("name") @DefaultValue("James") String name) {
        return hello(name);
    }

    @GET
    @Path("helloworld/queryparam")
    public String get(@QueryParam("name") @DefaultValue("Matt") String name) {
        return hello(name);
    }

    @PATCH
    @Path("helloworld/patch")
    public String patch(@QueryParam("name") @DefaultValue("James") String name) {
        return hello(name);
    }

    @GET
    @Path("helloworld/headerparam")
    public String header(@HeaderParam("name") String name) {
        return hello(name);
    }

    @GET
    @Path("helloworld/xff")
    public String xForwardedFor(@HeaderParam(X_FORWARDED_FOR) String forwardedFor) {
        return forwardedFor;
    }

    @GET
    @Path("helloworld/x-forwarded-proto")
    public String xForwardedProto(@HeaderParam(X_FORWARDED_PROTO) String forwardedProto) {
        return forwardedProto;
    }

    @POST
    @Path("helloworld/formparam")
    public String post(@FormParam("name") @DefaultValue("Dan") String name) {
        return hello(name);
    }

    @PUT
    @Path("echo")
    public InputStream echo(InputStream input) {
        return input;
    }

    @GET
    @Path("echoheaders")
    public String echoHeaders(HeaderParameters headers) {
        return headers.toString();
    }

    @GET
    @Path("echoquery")
    public String echoQueryParams(QueryParameters params) {
        return params.toString();
    }

    @GET
    @Path("goesbang")
    public String goBang(@QueryParam("exceptionMessage") String exceptionMessage) {
        throw new RuntimeException(exceptionMessage);
    }

    @GET
    @Path("html")
    @Produces(MediaType.TEXT_HTML)
    public String html() {
        return "<html><body>some content</body></html>";
    }

    @ANY
    @Path("any")
    public Response any() {
        return response().header("x-custom-header", "smile").entity(hello("everyone")).build();
    }

    @DELETE
    @Path("delete")
    public String delete() {
        return "The contents of your hard drive have been deleted";
    }

    @GET
    @Path("optionalDate")
    public String optionalDate(@QueryParam("date") Option<Date> date) {
        return date.map(Dates.format(Dates.LEXICAL())).getOrElse("no date");
    }

    @GET
    @Path("optionalInteger")
    public String optionalInteger(@QueryParam("integer") Option<Integer> integer) {
        return integer.map(toString).getOrElse("no integer");
    }

    @GET
    @Path("stream-exception")
    public StreamingWriter streamException() {
        return writer -> {
            writer.write("Yes, I'm streaming");
            throw new RuntimeException("boom from streaming!");
        };
    }

    private String hello(String name) {
        return "Hello " + name;
    }
}
