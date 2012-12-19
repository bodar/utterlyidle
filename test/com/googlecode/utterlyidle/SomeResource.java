package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.QueryParam;

@Path("path/{id}")
public class SomeResource {
    @GET
    public String getHtmlWithQuery(@PathParam("id") String id, @QueryParam("foo") String foo) {
        return "bob";
    }

    @GET
    public String getHtml(@PathParam("id") String id) {
        return "bob";
    }

    @GET
    public String getHtml(@PathParam("id") Option<String> someValue) {
        return "bob";
    }

    @GET
    public StreamingOutput getStreamingHtml(@PathParam("id") String id) {
        return null;
    }

    @GET
    public StreamingWriter getStreamingWriter(@PathParam("id") String id) {
        return null;
    }

    @GET
    public void getOptional(@PathParam("id") String id, @QueryParam("foo") Option<String> value) {
    }

    @GET
    public String getWithQueryParameters(QueryParameters queryParameters) {
        return "bob";
    }
}
