package com.googlecode.utterlyidle;


import javax.ws.rs.core.HttpHeaders;

public class SeeOther implements Redirect {
    private final String location;

    private SeeOther(String location) {
        this.location = location;
    }

    public String location() {
        return location;
    }

    public void applyTo(BasePath base, Response response) {
        response.header(HttpHeaders.LOCATION, base.file(location).toString());
        response.code(Status.SEE_OTHER);
    }

    public static SeeOther seeOther(String location) {
        return new SeeOther(location);
    }

}