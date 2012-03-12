package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;

@Path("path/{id}")
public class CustomType {
    @GET
    public String getHtml(@PathParam("id") Id id) {
        return "bob";
    }

}
