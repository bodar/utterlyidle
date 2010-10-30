package com.googlecode.utterlyidle.httpserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class HelloWorld {
    @GET
    @Path("helloWorld")
    public String get(){
        return "Hello";
    }
}
