package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.handlers.ResponseHandlersFinder;
import com.googlecode.yadic.Container;

import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.InvocationTargetException;

import static com.googlecode.totallylazy.Exceptions.toException;
import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Right.right;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.comparators.Comparators.where;
import static com.googlecode.utterlyidle.Activator.ExtensionMethods.signature;
import static com.googlecode.utterlyidle.ConsumesMimeMatcher.contentMatches;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.MatchFailure.matchFailure;
import static com.googlecode.utterlyidle.MatchQuality.matchQuality;
import static com.googlecode.utterlyidle.MethodMatcher.methodMatches;
import static com.googlecode.utterlyidle.ParametersExtractor.parametersMatches;
import static com.googlecode.utterlyidle.PathMatcher.pathMatches;
import static com.googlecode.utterlyidle.ProducesMimeMatcher.producesMatches;
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
        setupContainer(request);
        final Either<MatchFailure, Activator> either = findActivator(request);
        if (either.isLeft()) {
            return handlers.findAndHandle(request, response(
                    either.left().status(),
                    headerParameters(pair(CONTENT_TYPE, TEXT_HTML)),
                    either.left()));
        }
        Response response = activate(either.right(), request);
        return handlers.findAndHandle(request, response);
    }

    private void setupContainer(Request request) {
        container.addInstance(Request.class, request);
        activators().fold(container, new Callable2<Container, Activator, Container>() {
            public Container call(Container container, Activator activator) throws Exception {
                Class<?> aClass = activator.method().getDeclaringClass();
                if(!container.contains(aClass)){
                    container.add(aClass);
                }
                return container;
            }
        });
    }

    private Either<MatchFailure, Activator> findActivator(final Request request) {
        final Either<MatchFailure, Sequence<Activator>> result = filter(
                pair(pathMatches(container.get(BasePath.class), request), Status.NOT_FOUND),
                pair(methodMatches(request), Status.METHOD_NOT_ALLOWED),
                pair(contentMatches(request), Status.UNSUPPORTED_MEDIA_TYPE),
                pair(producesMatches(request), Status.NOT_ACCEPTABLE),
                pair(parametersMatches(request, application), Status.UNSATISFIABLE_PARAMETERS)
        );

        if (result.isLeft()) {
            return left(result.left());
        }

        return right((Activator) result.right().sortBy(where(signature(), matchQuality(request))).head());
    }

    private Either<MatchFailure, Sequence<Activator>> filter(Pair<Predicate<HttpSignature>, Status>... filterAndResult) {
        Sequence<Activator> activators = activators();
        for (Pair<Predicate<HttpSignature>, Status> pair : filterAndResult) {
            Sequence<Activator> matchesSoFar = activators;
            activators = activators.filter(Predicates.where(signature(), pair.first()));
            if (activators.isEmpty()) {
                return left(matchFailure(pair.second(), matchesSoFar.map(signature())));
            }
        }
        return right(activators);
    }

    private Sequence<Activator> activators() {
        return sequence(this.activators.activators());
    }

    public Response activate(Activator activator, Request request) throws Exception {
        Class<?> declaringClass = activator.method().getDeclaringClass();
        Object instance = container.get(declaringClass);
        Object result = getResponse(activator, request, instance);
        if (result instanceof Response) {
            return (Response) result;
        }
        if (result instanceof Either) {
            result = ((Either) result).value();
        }

        return response().
                header(HttpHeaders.CONTENT_TYPE, activator.httpSignature().produces()).
                entity(result).
                status(Status.OK);
    }

    private Object getResponse(Activator activator, Request request, Object resourceInstance) throws Exception {
        try {
            Object[] arguments = new ParametersExtractor(activator.httpSignature().uriTemplate(), application, activator.httpSignature().parameters()).extract(request);
            return activator.method().invoke(resourceInstance, arguments);
        } catch (InvocationTargetException e) {
            throw toException(e.getCause());
        }
    }







}
