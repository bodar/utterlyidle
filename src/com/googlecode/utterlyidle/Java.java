package com.googlecode.utterlyidle;

public class Java {

    private Java() {}

    public static int majorVersion() {
        return Integer.parseInt(System.getProperty("java.version").split("[.-]")[0]);
    }

}
