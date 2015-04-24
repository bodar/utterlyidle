package com.googlecode.utterlyidle;

@Deprecated // Please use com.googlecode.totallylazy.security.Base64
public class Base64 {
    public static byte[] decode(String content) {
        return com.googlecode.totallylazy.security.Base64.decode(content);
    }

    public static String encode(byte[] content) {
        return com.googlecode.totallylazy.security.Base64.encode(content);
    }
}
