package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.Rfc2616;

public class CookieName {
    private final String value;

    public static CookieName cookieName(String value) {
        return new CookieName(value);
    }
    public CookieName(String value) {
        if(value==null) value = "";
        if(!Rfc2616.isValidToken(value)) throw new IllegalArgumentException("Cookie name '" + value + "' is not valid. Should be a token. See RFC 2965 and RFC 2616.");
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CookieName that = (CookieName) o;

        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
