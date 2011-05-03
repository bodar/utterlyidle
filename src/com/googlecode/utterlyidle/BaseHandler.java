package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
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
import static com.googlecode.totallylazy.comparators.Comparators.where;
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
    private final Application application;

    public BaseHandler(Activators activators, ResponseHandlersFinder handlers, Container container, Application application) {
        this.activators = activators;
        this.handlers = handlers;
        this.container = container;
        this.application = application;
    }

    public Response handle(Request request) throws Exception {
        Container resolver = container.addInstance(Request.class, request);
        BasePath basePath = container.get(BasePath.class);
        final Either<MatchFailure, Activator> either = findActivator(basePath, request);
        if (either.isLeft()) {
            return handlers.findAndHandle(request, response(
                    either.left().status(),
                    headerParameters(pair(CONTENT_TYPE, TEXT_HTML)),
                    either.left()));
        }
        return handlers.findAndHandle(request, either.right().activate(resolver, request, application));
    }

    private Either<MatchFailure, Activator> findActivator(BasePath basePath, final Request request) {
        final Either<MatchFailure, Sequence<Activator>> result = filter(
                pair(pathMatches(basePath, request), Status.NOT_FOUND),
                pair(methodMatches(request), Status.METHOD_NOT_ALLOWED),
                pair(contentMatches(request), Status.UNSUPPORTED_MEDIA_TYPE),
                pair(producesMatches(request), Status.NOT_ACCEPTABLE),
                pair(argumentsMatches(request), Status.UNSATISFIABLE_PARAMETERS)
        );

        if (result.isLeft()) {
            return left(result.left());
        }

        return right((Activator) result.right().sortBy(where(signature(), matchQuality(request))).head());
    }

    private Callable1<Activator, HttpSignature> signature() {
        return new Callable1<Activator, HttpSignature>() {
            public HttpSignature call(Activator activator) throws Exception {
                return activator.httpSignature();
            }
        };
    }

    private Either<MatchFailure, Sequence<Activator>> filter(Pair<Predicate<Activator>, Status>... filterAndResult) {
        Sequence<Activator> activators = sequence(this.activators.activators());
        for (Pair<Predicate<Activator>, Status> pair : filterAndResult) {
            Sequence<Activator> matchesSoFar = activators;
            activators = activators.filter(pair.first());
            if (activators.isEmpty()) {
                return left(matchFailure(pair.second(), matchesSoFar));
            }
        }
        return right(activators);
    }


    private Predicate<Activator> argumentsMatches(final Request request) {
        return new Predicate<Activator>() {
            public boolean matches(Activator httpMethodActivator) {
                return new ParametersExtractor(httpMethodActivator.httpSignature().uriTemplate(), application, httpMethodActivator.httpSignature().parameters()).matches(request);
            }
        };
    }

    private Predicate<Activator> producesMatches(final Request request) {
        return new Predicate<Activator>() {
            public boolean matches(Activator httpMethodActivator) {
                return new ProducesMimeMatcher(httpMethodActivator.httpSignature().produces()).matches(request);
            }
        };
    }

    private Predicate<Activator> contentMatches(final Request request) {
        return new Predicate<Activator>() {
            public boolean matches(Activator httpMethodActivator) {
                return new ConsumesMimeMatcher(httpMethodActivator.httpSignature().consumes()).matches(request);
            }
        };
    }

    private Predicate<Activator> methodMatches(final Request request) {
        return new Predicate<Activator>() {
            public boolean matches(Activator httpMethodActivator) {
                return  new MethodMatcher(httpMethodActivator.httpSignature().httpMethod()).matches(request);
            }
        };
    }

    private Predicate<Activator> pathMatches(final BasePath basePath, final Request request) {
        return new Predicate<Activator>() {
            public boolean matches(Activator httpMethodActivator) {
                return new PathMatcher(basePath, httpMethodActivator.httpSignature().uriTemplate()).matches(request);
            }
        };
    }

}
