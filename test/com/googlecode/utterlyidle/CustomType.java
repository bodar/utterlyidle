package com.googlecode.utterlyidle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("path/{id}")
public class CustomType {
    @GET
    public String getHtml(@PathParam("id") Id id) {
        return "bob";
    }

}
