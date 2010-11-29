package com.googlecode.utterlyidle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

@Path("path/{id}")
public class NoDefaultConstructor {
    NoDefaultConstructor(SomeResource someResource) {
    }

    @GET
    public String getHtml(@PathParam("id") String id) {
        return "bob";
    }

    @GET
    public StreamingOutput getStreamingHtml(@PathParam("id") String id) {
        return null;
    }
}
