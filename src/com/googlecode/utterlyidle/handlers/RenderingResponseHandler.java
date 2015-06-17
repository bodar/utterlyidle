package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Callers;
import com.googlecode.totallylazy.Unchecked;
import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.modules.DependsOnContainer;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.SimpleContainer;

import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Unchecked.cast;


public abstract class RenderingResponseHandler<T> implements ResponseHandler {
    public static <T> RenderingResponseHandler<T> renderer(Class<? extends Renderer<T>> renderer) {
        return new ClassRenderingResponseHandler<T>(renderer);
    }

    public static <T> RenderingResponseHandler<T> renderer(Function1<Resolver, ? extends Renderer<T>> renderer) {
        return new CallableRenderingResponseHandler<T>(renderer);
    }

    public static <T> RenderingResponseHandler<T> renderer(Type renderer) {
        return new ClassRenderingResponseHandler<T>(renderer);
    }

    public static <T> RenderingResponseHandler<T> renderer(Renderer<T> renderer) {
        return new InstanceRenderingResponseHandler<T>(renderer);
    }

    public Response handle(Response response) throws Exception {
        return ResponseBuilder.modify(response).entity(getRenderer().render(Unchecked.<T>cast(response.entity().value()))).build();
    }

    protected abstract Renderer<T> getRenderer() throws Exception;

    private static class ClassRenderingResponseHandler<T> extends RenderingResponseHandler<T> implements DependsOnContainer {
        private final Type renderer;
        private Container container;

        public ClassRenderingResponseHandler(Type renderer) {
            this.renderer = renderer;
        }

        protected Renderer<T> getRenderer() throws Exception {
            if(!container.contains(renderer)){
                return cast(new SimpleContainer(container).addType(renderer, renderer).resolve(renderer));
            }
            return cast(container.resolve(renderer));
        }

        @Override
        public void setContainer(Container container) throws Exception {
            this.container = container;
        }
    }

    private static class CallableRenderingResponseHandler<T> extends RenderingResponseHandler<T> implements DependsOnContainer {
        private final Function1<Resolver, ? extends Renderer<T>> callable;
        private Container container;

        public CallableRenderingResponseHandler(final Function1<Resolver, ? extends Renderer<T>> callable) {
            this.callable = callable;
        }

        @Override
        protected Renderer<T> getRenderer() throws Exception {
            return Callers.call(callable, container);
        }

        @Override
        public void setContainer(final Container container) throws Exception {
            this.container = container;
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
