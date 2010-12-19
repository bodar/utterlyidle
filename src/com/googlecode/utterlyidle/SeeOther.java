package com.googlecode.utterlyidle;


public class SeeOther implements Redirect {
    private final String location;

    private SeeOther(String location) {
        this.location = location;
    }

    public String location() {
        return location;
    }

    public static SeeOther seeOther(String location) {
        return new SeeOther(location);
    }

}