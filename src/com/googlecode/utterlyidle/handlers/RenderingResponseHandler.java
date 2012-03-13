package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.modules.DependsOnContainer;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;


public abstract class RenderingResponseHandler<T> implements ResponseHandler {
    public static <T> RenderingResponseHandler<T> renderer(Class<? extends Renderer<T>> renderer) {
        return new ClassRenderingResponseHandler<T>(renderer);
    }

    public static <T> RenderingResponseHandler<T> renderer(Renderer<T> renderer) {
        return new InstanceRenderingResponseHandler<T>(renderer);
    }

    public Response handle(Response response) throws Exception {
        return ResponseBuilder.modify(response).entity(getRenderer().render((T) response.entity())).build();
    }

    protected abstract Renderer<T> getRenderer();

    private static class ClassRenderingResponseHandler<T> extends RenderingResponseHandler<T> implements DependsOnContainer {
        private final Class<? extends Renderer<T>> renderer;
        private Container container;

        public ClassRenderingResponseHandler(Class<? extends Renderer<T>> renderer) {
            this.renderer = renderer;
        }

        protected Renderer<T> getRenderer() {
            if(!container.contains(renderer)){
                return new SimpleContainer(container).add(renderer).get(renderer);
            }
            return container.get(renderer);
        }

        @Override
        public void setContainer(Container container) throws Exception {
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
