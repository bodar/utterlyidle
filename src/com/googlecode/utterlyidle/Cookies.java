package com.googlecode.utterlyidle;

import java.util.HashMap;
import java.util.Map;

public class Cookies {
    private final Map<String, String> cookies = new HashMap<String, String>();

    public Cookies add(String name, String value){
        cookies.put(name, value);
        return this;
    }
}
