package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.handlers.RendererHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;

import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Right.right;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.MatchFailure.matchFailure;
import static com.googlecode.utterlyidle.MatchQuality.matchQuality;

public class RestEngine implements Engine {
    private final List<HttpMethodActivator> activators = new ArrayList<HttpMethodActivator>();
    private final ResponseHandlers handlers = new ResponseHandlers();
    private final RendererHandler renderers = new RendererHandler();

    public RendererHandler renderers() {
        return renderers;
    }

    public ResponseHandlers responseHandlers() {
        return handlers;
    }

    public void add(Class resource) {
        for (final Method method : resource.getMethods()) {
            for (final HttpMethod httpMethod : new HttpMethodExtractor().extract(method)) {
                activators.add(new HttpMethodActivator(httpMethod.value(), method));
            }
        }
    }

    public Either<MatchFailure, HttpMethodActivator> findActivator(final Request request) {
        final Either<MatchFailure, Sequence<HttpMethodActivator>> result = filter(
                pair(pathMatches(request), Status.NOT_FOUND),
                pair(methodMatches(request), Status.METHOD_NOT_ALLOWED),
                pair(contentMatches(request), Status.UNSUPPORTED_MEDIA_TYPE),
                pair(producesMatches(request), Status.NOT_ACCEPTABLE),
                pair(argumentsMatches(request), Status.UNSATISFIABLE_PARAMETERS)
        );

        if (result.isLeft()) {
            return left(result.left());
        }

        return right(result.right().sortBy(matchQuality(request)).head());
    }

    private Either<MatchFailure, Sequence<HttpMethodActivator>> filter(Pair<Predicate<HttpMethodActivator>, Status>... filterAndResult) {
        Sequence<HttpMethodActivator> sequence = sequence(activators);
        for (Pair<Predicate<HttpMethodActivator>, Status> pair : filterAndResult) {
            Sequence<HttpMethodActivator> matchesSoFar = sequence;
            sequence = sequence.filter(pair.first());
            if (sequence.isEmpty()) {
                return left(matchFailure(pair.second(), matchesSoFar));
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

}