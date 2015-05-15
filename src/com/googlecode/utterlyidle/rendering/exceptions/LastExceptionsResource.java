package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Maps;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Hidden;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.Produces;

import java.util.Map;

import static com.googlecode.totallylazy.Sequences.sequence;

@Produces(MediaType.TEXT_HTML)
@Path(LastExceptionsResource.PATH)
@Hidden
public class LastExceptionsResource {
    public static final String PATH = "utterlyidle/exceptions";

    private LastExceptions lastExceptions;
    private BasePath basePath;

    public LastExceptionsResource(LastExceptions lastExceptions, BasePath basePath) {
        this.lastExceptions = lastExceptions;
        this.basePath = basePath;
    }

    @GET
    public Map<String, Object> allRecordedExceptions() {
        return Maps.map(
                "base", basePath,
                "exceptions", sequence(lastExceptions).reverse().toList(),
                "maximumSize", lastExceptions.getMaximumSize());
    }
}
