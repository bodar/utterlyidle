package com.googlecode.utterlyidle.rendering;

import com.googlecode.utterlyidle.Hidden;
import com.googlecode.utterlyidle.RegisteredResources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.utterlyidle.io.Converter.asString;

public class BuiltInResources {
    @GET
    @Path("utterlyidle/{filename:.+\\.js}")
    @Produces("text/javascript")
    @Hidden
    public String getJavascript(@PathParam("filename") String filename){
        return asString(RegisteredResources.class.getResourceAsStream(filename));
    }

    @GET
    @Path("utterlyidle/{filename:.+\\.css}")
    @Produces("text/css")
    @Hidden
    public String getCss(@PathParam("filename") String filename){
        return asString(RegisteredResources.class.getResourceAsStream(filename));
    }

    @GET
    @Path("utterlyidle/{filename:.+\\.png}")
    @Produces("image/png")
    @Hidden
    public byte[] getPng(@PathParam("filename") String filename){
        return bytes(RegisteredResources.class.getResourceAsStream(filename));
    }
}
