package com.googlecode.utterlyidle.httpserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public class HelloWorld {
    @GET
    @Path("helloWorld")
    public String get(@QueryParam("name") String name){
        return "Hello " + name;
    }
}
