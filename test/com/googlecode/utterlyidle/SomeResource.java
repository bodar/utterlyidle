package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.None;
import com.googlecode.totallylazy.Option;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.StreamingOutput;

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
}
