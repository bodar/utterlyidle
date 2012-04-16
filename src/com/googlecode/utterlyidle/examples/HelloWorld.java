package com.googlecode.utterlyidle.examples;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.numbers.Numbers;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Responses;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.StreamingOutput;
import com.googlecode.utterlyidle.annotations.DefaultValue;
import com.googlecode.utterlyidle.annotations.FormParam;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.HeaderParam;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.annotations.QueryParam;

import static com.googlecode.utterlyidle.Entity.streamingOutputOf;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.Responses.response;

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
        return ResponseBuilder.response(Status.OK).header(HttpHeaders.CACHE_CONTROL, "public, max-age=60").entity("cacheable").build();
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
        return ResponseBuilder.response(Status.OK).header("greeting", hello(name)).entity("").build();
    }

    @GET
    @Path("helloworld/queryparam")
    public String get(@QueryParam("name") @DefaultValue("Matt") String name) {
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

    @POST
    @Path("helloworld/formparam")
    public String post(@FormParam("name") @DefaultValue("Dan") String name) {
        return hello(name);
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


    private String hello(String name) {
        return "Hello " + name;
    }
}
