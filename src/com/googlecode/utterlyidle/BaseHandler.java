package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.handlers.ResponseHandlersFinder;
import com.googlecode.yadic.Container;

import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Right.right;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.MatchFailure.matchFailure;
import static com.googlecode.utterlyidle.MatchQuality.matchQuality;
import static com.googlecode.utterlyidle.Responses.response;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

public class BaseHandler implements HttpHandler {
    private final Activators activators;
    private final ResponseHandlersFinder handlers;
    private final Container container;

    public BaseHandler(Activators activators, ResponseHandlersFinder handlers, Container container) {
        this.activators = activators;
        this.handlers = handlers;
        this.container = container;
    }

    public Response handle(Request request) throws Exception {
        Container resolver = container.addInstance(Request.class, request);
        final Either<MatchFailure, Activator> either = findActivator(request);
        if (either.isLeft()) {
            return handlers.findAndHandle(request, response(
                    either.left().status(),
                    headerParameters(pair(CONTENT_TYPE, TEXT_HTML)),
                    either.left()));
        } else {
            return handlers.findAndHandle(request, either.right().activate(resolver, request));
        }
    }

    private Either<MatchFailure, Activator> findActivator(final Request request) {
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

        return right((Activator) result.right().sortBy(matchQuality(request)).head());
    }

    private Either<MatchFailure, Sequence<HttpMethodActivator>> filter(Pair<Predicate<HttpMethodActivator>, Status>... filterAndResult) {
        Sequence<HttpMethodActivator> sequence = sequence(activators.activators());
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
