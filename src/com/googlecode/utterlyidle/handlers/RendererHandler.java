package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RendererHandler implements ResponseHandler<Object> {
    private final Map<Class<?>, Renderer<?>> renderers = new HashMap<Class<?>, Renderer<?>>();

    public void handle(Object value, Response response) throws IOException {
        final Renderer renderer = renderers.get(value.getClass());
        response.write(renderer.render(value));
    }

    public <T> void add(Class<T> customClass, Renderer<T> renderer) {
        renderers.put(customClass, renderer);
    }
}
