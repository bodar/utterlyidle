package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.handlers.NullHandler;
import com.googlecode.utterlyidle.handlers.RedirectHandler;
import com.googlecode.utterlyidle.handlers.RendererHandler;
import com.googlecode.utterlyidle.handlers.StreamingOutputHandler;
import com.googlecode.utterlyidle.handlers.StreamingWriterHandler;
import com.googlecode.utterlyidle.handlers.StringHandler;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Right.right;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.MatchQuality.matchQuality;
import static com.googlecode.utterlyidle.ResponseBody.ignoreContent;

public class RestEngine implements Engine {
    private final List<HttpMethodActivator> activators = new ArrayList<HttpMethodActivator>();
    private final Map<Class<?>, ResponseHandler<?>> handlers = new HashMap<Class<?>, ResponseHandler<?>>();
    private final RendererHandler renderers = new RendererHandler();

    public RestEngine() {
        handlers.put(String.class, new StringHandler());
        handlers.put(StreamingWriter.class, new StreamingWriterHandler());
        handlers.put(StreamingOutput.class, new StreamingOutputHandler());
    }

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
        final Either<Status, HttpMethodActivator> either = findActivator(request);
        if (either.isLeft()) {
            handle(ignoreContent(), request, response.code(either.left()));
        } else {
            final ResponseBody responseBody = either.right().activate(container, request);
            handle(responseBody, request, response);
        }
    }

    public Either<Status, HttpMethodActivator> findActivator(final Request request) {
        final Either<Status, Sequence<HttpMethodActivator>> result = filter(
                pair(pathMatches(request), Status.NOT_FOUND),
                pair(methodMatches(request), Status.METHOD_NOT_ALLOWED),
                pair(contentMatches(request), Status.UNSUPPORTED_MEDIA_TYPE),
                pair(producesMatches(request), Status.NOT_ACCEPTABLE),
                pair(argumentsMatches(request), Status.BAD_REQUEST)
        );

        if (result.isLeft()) {
            return left(result.left());
        }

        return right(result.right().sortBy(matchQuality(request)).head());
    }

    private Either<Status, Sequence<HttpMethodActivator>> filter(Pair<Predicate<HttpMethodActivator>, Status>... filterAndResult) {
        Sequence<HttpMethodActivator> sequence = sequence(activators);
        for (Pair<Predicate<HttpMethodActivator>, Status> pair : filterAndResult) {
            sequence = sequence.filter(pair.first());
            if (sequence.isEmpty()) {
                return left(pair.second());
            }
        }
        return right(sequence);
    }

    private Predicate<HttpMethodActivator> argumentsMatches(final Request request) {
        return new Predicate<HttpMethodActivator>() {
            public boolean matches(HttpMethodActivator httpMethodActivator) {
                return httpMethodActivator.argumentMatcher().matches(request);
            }
        };
    }

    private Predicate<HttpMethodActivator> producesMatches(final Request request) {
        return new Predicate<HttpMethodActivator>() {
            public boolean matches(HttpMethodActivator httpMethodActivator) {
                return httpMethodActivator.producesMatcher().matches(request);
            }
        };
    }

    private Predicate<HttpMethodActivator> contentMatches(final Request request) {
        return new Predicate<HttpMethodActivator>() {
            public boolean matches(HttpMethodActivator httpMethodActivator) {
                return httpMethodActivator.consumesMatcher().matches(request);
            }
        };
    }

    private Predicate<HttpMethodActivator> methodMatches(final Request request) {
        return new Predicate<HttpMethodActivator>() {
            public boolean matches(HttpMethodActivator httpMethodActivator) {
                return httpMethodActivator.methodMatcher().matches(request);
            }
        };
    }

    private Predicate<HttpMethodActivator> pathMatches(final Request request) {
        return new Predicate<HttpMethodActivator>() {
            public boolean matches(HttpMethodActivator httpMethodActivator) {
                return httpMethodActivator.pathMatcher().matches(request);
            }
        };
    }

    public <T> void addRenderer(Class<T> customClass, Renderer<T> renderer) {
        renderers.add(customClass, renderer);
    }

    private void handle(ResponseBody responseBody, Request request, Response response) {
        try {
            response.header(HttpHeaders.CONTENT_TYPE, responseBody.mimeType());

            Object result = responseBody.value();
            getHandlerFor(result, request).handle(result, response);

            response.flush();
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }

    }

    private ResponseHandler getHandlerFor(Object instance, Request request) {
        if(instance == null){
            return new NullHandler();
        }
        if(instance instanceof Redirect){
            return new RedirectHandler(request);
        }

        if (handlers.containsKey(instance.getClass())) {
            return handlers.get(instance.getClass());
        }

        return renderers;
    }

}