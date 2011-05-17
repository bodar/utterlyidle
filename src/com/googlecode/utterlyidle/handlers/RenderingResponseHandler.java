package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.modules.DependsOnResolver;
import com.googlecode.yadic.Resolver;

import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.googlecode.yadic.resolvers.Resolvers.create;
import static com.googlecode.yadic.resolvers.Resolvers.resolve;


public abstract class RenderingResponseHandler<T> implements ResponseHandler {
    public static <T> RenderingResponseHandler<T> renderer(Class<? extends Renderer<T>> renderer) {
        return new ClassRenderingResponseHandler<T>(renderer);
    }

    public static <T> RenderingResponseHandler<T> renderer(Renderer<T> renderer) {
        return new InstanceRenderingResponseHandler<T>(renderer);
    }

    public Response handle(Response response) throws Exception {
        Writer writer = new OutputStreamWriter(response.output());
        writer.write(getRenderer().render((T) response.entity()));
        writer.flush();
        return response;
    }

    protected abstract Renderer<T> getRenderer();

    private static class ClassRenderingResponseHandler<T> extends RenderingResponseHandler<T> implements DependsOnResolver{
        private final Class<? extends Renderer<T>> renderer;
        private Resolver resolver;

        public ClassRenderingResponseHandler(Class<? extends Renderer<T>> renderer) {
            this.renderer = renderer;
        }

        protected Renderer<T> getRenderer() {
            return (Renderer<T>) resolve(create(renderer, resolver), renderer);
        }

        public void setResolver(Resolver resolver) {
            this.resolver = resolver;
        }

    }

    private static class InstanceRenderingResponseHandler<T> extends RenderingResponseHandler<T> {
        private final Renderer<T> renderer;

        public InstanceRenderingResponseHandler(Renderer<T> renderer) {
            this.renderer = renderer;
        }

        @Override
        protected Renderer<T> getRenderer() {
            return renderer;
        }
    }
}
