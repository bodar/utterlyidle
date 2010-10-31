package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Hidden;
import com.googlecode.utterlyidle.RestEngine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static com.googlecode.utterlyidle.io.Converter.asString;

public class HelloWorld {
    @GET
    @Path("helloWorld")
    public String get(@QueryParam("name") String name){
        return "Hello " + name;
    }

    @GET
    @Path("utterlyidle/{filename:.+\\.js}")
    @Produces("text/javascript")
    @Hidden
    public String getJavascript(@PathParam("filename") String filename){
        return asString(RestEngine.class.getResourceAsStream(filename));
    }
}
