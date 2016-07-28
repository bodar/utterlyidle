package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Unchecked;
import com.googlecode.totallylazy.functions.Function0;
import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Container;

import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Unchecked.cast;


public class RenderingResponseHandler<T> implements ResponseHandler {
    private Function0<Renderer<T>> renderer;

    public RenderingResponseHandler(final Function0<Renderer<T>> renderer) {
        this.renderer = renderer;
    }

    public static <T> RenderingResponseHandler<T> renderer(Function0<Renderer<T>> renderer) {
        return new RenderingResponseHandler<T>(renderer);
    }

    public static <T> RenderingResponseHandler<T> renderer(Container requestScope, Type renderer) {
        return renderer(() -> {
            if (requestScope.contains(renderer)) return cast(requestScope.getResolver(renderer).resolve(renderer));
            return requestScope.create(renderer);
        });
    }

    public static <T> RenderingResponseHandler<T> renderer(Renderer<T> renderer) {
        return renderer(() -> renderer);
    }

    public Response handle(Response response) throws Exception {
        return response.entity(renderer.apply().render(Unchecked.<T>cast(response.entity().value())));
    }
}
