package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Renderer;

public class ObjectRenderer implements Renderer<Object> {
    public String render(Object value) {
        return value.toString();
    }
}
