package com.googlecode.utterlyidle.cookies;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CookieAttribute {
    public static final SimpleDateFormat RFC822_DATE_FORMAT = new SimpleDateFormat("EE, dd-MMM-yyyy HH:mm:ss");

    public static final String COMMENT = "Comment";
    public static final String DOMAIN = "Domain";
    public static final String MAX_AGE = "Max-Age";
    public static final String PATH = "Path";
    public static final String SECURE = "Secure";
    public static final String EXPIRES = "Expires";

    private final String name;
    private final String value;

    public static CookieAttribute cookieAttribute(String name, String value) {
        return new CookieAttribute(name, value);
    }

    public CookieAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static CookieAttribute comment(String value){
        return cookieAttribute(COMMENT, value);
    }

    public static CookieAttribute domain(String value){
        return cookieAttribute(DOMAIN, value);
    }

    public static CookieAttribute maxAge(long seconds){
        return cookieAttribute(MAX_AGE, String.valueOf(seconds));
    }

    public static CookieAttribute path(String value){
        return cookieAttribute(PATH, value);
    }

    public static CookieAttribute secure(){
        return cookieAttribute(SECURE, "");
    }

    public static CookieAttribute expires(Date gmtDate){
        return cookieAttribute(EXPIRES, RFC822_DATE_FORMAT.format(gmtDate) + " GMT");
    }

    @Override
    public String toString() {
        return String.format("%s=\"%s\"", name, value);
    }
}
