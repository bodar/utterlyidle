package com.googlecode.utterlyidle.rendering;

import com.googlecode.utterlyidle.Hidden;
import com.googlecode.utterlyidle.AnnotatedResources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static com.googlecode.utterlyidle.io.Converter.asString;

public class BuiltInResources {
    @GET
    @Path("utterlyidle/{filename:.+\\.js}")
    @Produces("text/javascript")
    @Hidden
    public String getJavascript(@PathParam("filename") String filename){
        return asString(AnnotatedResources.class.getResourceAsStream(filename));
    }
}
