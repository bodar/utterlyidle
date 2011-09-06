package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.StreamingOutput;

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
