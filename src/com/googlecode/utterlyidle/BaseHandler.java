package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.handlers.ResponseHandlersFinder;
import com.googlecode.yadic.Container;

import java.lang.reflect.InvocationTargetException;

import static com.googlecode.totallylazy.Exceptions.toException;
import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Right.right;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Accept.accept;
import static com.googlecode.utterlyidle.ConsumesMimeMatcher.contentMatches;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.MatchFailure.matchFailure;
import static com.googlecode.utterlyidle.MatchQuality.matchQuality;
import static com.googlecode.utterlyidle.MediaType.TEXT_HTML;
import static com.googlecode.utterlyidle.MethodMatcher.methodMatches;
import static com.googlecode.utterlyidle.ParametersExtractor.parametersMatches;
import static com.googlecode.utterlyidle.PathMatcher.pathMatches;
import static com.googlecode.utterlyidle.ProducesMimeMatcher.producesMatches;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.UrlEncodedMessage.DEFAULT_CHARSET;

public class BaseHandler implements HttpHandler {
    private final Bindings bindings;
    private final ResponseHandlersFinder handlers;
    private final Container container;
    private final Application application;

    public BaseHandler(Bindings bindings, ResponseHandlersFinder handlers, Container container, Application application) {
        this.bindings = bindings;
        this.handlers = handlers;
        this.container = container;
        this.application = application;
    }

    public Response handle(final Request request) throws Exception {
        setupContainer(request);
        return handlers.findAndHandle(request, getResponse(request));
    }

    private Response getResponse(final Request request) throws Exception {
        Either<MatchFailure, Sequence<Binding>> failureOrBindings = filter(
                pair(pathMatches(request), Status.NOT_FOUND),
                pair(methodMatches(request), Status.METHOD_NOT_ALLOWED),
                pair(contentMatches(request), Status.UNSUPPORTED_MEDIA_TYPE),
                pair(producesMatches(request), Status.NOT_ACCEPTABLE),
                pair(parametersMatches(request, application), Status.UNSATISFIABLE_PARAMETERS)
        );
        return failureOrBindings.map(failure(), success(request));
    }

    private Response success(final Request request, final Sequence<Binding> matchedBindings) throws Exception {
        Binding binding = findBestMatch(request, matchedBindings);
        return setContentType(accept(request).bestMatch(binding.produces()),
                wrapInResponse(
                        unwrapEither(
                                invokeMethod(binding, request))));
    }

    private Response failure(final MatchFailure matchFailure) {
        return response(
                matchFailure.status(),
                headerParameters(pair(CONTENT_TYPE, TEXT_HTML)),
                matchFailure);
    }

    private Response setContentType(String mimeType, Response response) {
        if (response.header(HttpHeaders.CONTENT_TYPE) == null) {
            return response.header(HttpHeaders.CONTENT_TYPE, defaultIfCharsetNotSpecified(mimeType));
        }
        return response;
    }

    private String defaultIfCharsetNotSpecified(String mimeType) {
        if (!mimeType.contains("charset")) {
            return mimeType + "; charset=\"" + DEFAULT_CHARSET + "\"";
        }
        return mimeType;
    }

    private Binding findBestMatch(Request request, final Sequence<Binding> bindings) {
        return bindings.sortBy(matchQuality(request)).head();
    }

    private Either<MatchFailure, Sequence<Binding>> filter(Pair<Predicate<Binding>, Status>... filterAndResult) {
        Sequence<Binding> activators = sequence(bindings);
        for (Pair<Predicate<Binding>, Status> pair : filterAndResult) {
            Sequence<Binding> matchesSoFar = activators;
            activators = activators.filter(pair.first());
            if (activators.isEmpty()) {
                return left(matchFailure(pair.second(), matchesSoFar));
            }
        }
        return right(activators);
    }

    private Object invokeMethod(Binding binding, Request request) throws Exception {
        try {
            Class<?> declaringClass = binding.method().getDeclaringClass();
            Object resourceInstance = container.get(declaringClass);
            Object[] arguments = new ParametersExtractor(binding.uriTemplate(), application, binding.parameters()).extract(request);
            return binding.method().invoke(resourceInstance, arguments);
        } catch (InvocationTargetException e) {
            throw toException(e.getCause());
        }
    }

    private Response wrapInResponse(Object instance) {
        if (instance instanceof Response) {
            return (Response) instance;
        }

        return response().entity(instance);
    }

    private Object unwrapEither(Object instance) {
        if (instance instanceof Either) {
            return ((Either) instance).value();
        }
        return instance;
    }

    private void setupContainer(Request request) {
        container.remove(Request.class);
        container.addInstance(Request.class, request);
        sequence(bindings).fold(container, new Callable2<Container, Binding, Container>() {
            public Container call(Container container, Binding binding) throws Exception {
                Class<?> aClass = binding.method().getDeclaringClass();
                if (!container.contains(aClass)) {
                    container.add(aClass);
                }
                return container;
            }
        });
    }

    private Callable1<? super Sequence<Binding>, Response> success(final Request request) {
        return new Callable1<Sequence<Binding>, Response>() {
            @Override
            public Response call(Sequence<Binding> bindingSequence) throws Exception {
                return success(request, bindingSequence);
            }
        };
    }

    private Callable1<? super MatchFailure, Response> failure() {
        return new Callable1<MatchFailure, Response>() {
            @Override
            public Response call(MatchFailure matchFailure) throws Exception {
                return failure(matchFailure);
            }
        };
    }
}
