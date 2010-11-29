package com.googlecode.utterlyidle.rendering;

import com.googlecode.utterlyidle.Renderer;

public class ObjectRenderer implements Renderer<Object> {
    public String render(Object value) {
        return value.toString();
    }
}
