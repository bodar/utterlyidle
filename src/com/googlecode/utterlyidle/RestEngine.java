package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Sequences.sequence;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

public class RestEngine implements Engine {
    List<HttpMethodActivator> activators = new ArrayList<HttpMethodActivator>();
    private Map<Class<?>, Renderer<?>> renderers = new HashMap<Class<?>, Renderer<?>>();

    public void add(Class resource) {
        for (final Method method : resource.getMethods()) {
            for (final HttpMethod httpMethod : getHttpMethod(method)) {
                activators.add(new HttpMethodActivator(httpMethod.value(), method));
            }
        }
    }

    public Option<HttpMethod> getHttpMethod(Method method) {
        return sequence(method.getAnnotations()).tryPick(new Callable1<Annotation, Option<HttpMethod>>() {
            public Option<HttpMethod> call(Annotation annotation) throws Exception {
                return sequence(annotation.annotationType().getDeclaredAnnotations()).safeCast(HttpMethod.class).headOption();
            }
        });
    }

    public void handle(Resolver container, Request request, Response response) {
        final ResponseBody responseBody = findActivator(request).get().activate(container, request);
        render(responseBody, request, response);
    }

    private void render(ResponseBody responseBody, Request request, Response response) {
        try {
            response.header(HttpHeaders.CONTENT_TYPE, responseBody.mimeType());
            
            Object result = responseBody.value();
            if (result == null) {
                response.code(NO_CONTENT);
            }
            else if (result instanceof Redirect) {
                ((Redirect) result).applyTo(request.base(), response);
            }
            else if (result instanceof String) {
                response.write((String) result);
            }
            else if (result instanceof StreamingOutput) {
                ((StreamingOutput) result).write(response.output());
            }
            else if (result instanceof StreamingWriter) {
                ((StreamingWriter) result).write(response.writer());
            }
            else if (renderers.containsKey(result.getClass())) {
                final Renderer renderer = renderers.get(result.getClass());
                response.write(renderer.render(result));
            }
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }

    }

    public Option<HttpMethodActivator> findActivator(final Request request) {
        return sequence(activators).filter(new Predicate<HttpMethodActivator>() {
            public boolean matches(HttpMethodActivator httpMethodActivator) {
                return httpMethodActivator.matches(request);
            }
        }).sortBy(matchQuality(request)).headOption();
    }

    private Comparator<? super HttpMethodActivator> matchQuality(final Request request) {
        return new Comparator<HttpMethodActivator>() {
            public int compare(HttpMethodActivator first, HttpMethodActivator second) {
                float firstQuality = first.matchQuality(request);
                float secondQuality = second.matchQuality(request);
                if (firstQuality == secondQuality)
                    return second.numberOfArguments() - first.numberOfArguments();
                return firstQuality > secondQuality ? -1 : 1;
            }
        };
    }

    public <T> void addRenderer(Class<T> customClass, Renderer<T> renderer) {
        renderers.put(customClass, renderer);
    }
}