package com.googlecode.utterlyidle.httpserver;

import javax.ws.rs.*;

public class HelloWorld {
    @GET
    @Path("helloWorld")
    public String get(@QueryParam("name") String name){
        return "Hello " + name;
    }

    @POST
    @Path("echo")
    public String post(@FormParam("value") String value){
        return value;
    }
}
