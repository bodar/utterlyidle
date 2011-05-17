package com.googlecode.utterlyidle.rendering;

import com.googlecode.utterlyidle.Hidden;
import com.googlecode.utterlyidle.RegisteredResources;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Produces;

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
