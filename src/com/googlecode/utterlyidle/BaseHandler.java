package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.utterlyidle.bindings.BindingMatcher;
import com.googlecode.utterlyidle.bindings.MatchedBinding;
import com.googlecode.yadic.Container;

import static com.googlecode.utterlyidle.Accept.accept;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.MediaType.TEXT_HTML;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.bindings.MatchedBinding.constructors.matchedBinding;

public class BaseHandler implements HttpHandler {
    private final Container container;
    private final BindingMatcher bindingMatcher;

    public BaseHandler(BindingMatcher bindingMatcher, Container container) {
        this.bindingMatcher = bindingMatcher;
        this.container = container;
    }

    public Response handle(final Request request) throws Exception {
        setupContainer(request);
        return getResponse(request);
    }

    private Response getResponse(final Request request) throws Exception {
        Either<MatchFailure, Binding> bestMatch = bindingMatcher.match(request);
        return bestMatch.map(failure(), success(request));
    }

    private Response success(final Request request, final Binding binding) throws Exception {
        if (container.contains(MatchedBinding.class)) {
            container.remove(MatchedBinding.class);
        }
        container.addInstance(MatchedBinding.class, matchedBinding(binding));
        return setContentType(accept(request).bestMatch(binding.produces()),
                wrapInResponse(
                        unwrapEither(
                                convertNullToNoContent(binding.invoke(container)))));
    }

    private Object convertNullToNoContent(final Object instance) {
        if(instance == null) return Responses.response(Status.NO_CONTENT);
        return instance;
    }

    private Function1<? super Binding, Response> success(final Request request) {
        return binding -> success(request, binding);
    }


    private Response failure(final MatchFailure matchFailure) {
        return modify(response(matchFailure.status())).
                contentType(TEXT_HTML).
                entity(matchFailure).
                build();
    }

    private Response setContentType(String mimeType, Response response) {
        if (header(response, CONTENT_TYPE) == null) {
            return modify(response).
                    contentType(defaultIfCharsetNotSpecified(mimeType)).
                    build();
        }
        return response;
    }

    private String defaultIfCharsetNotSpecified(String mimeType) {
        if (!mimeType.contains("charset")) {
            return mimeType + "; charset=\"" + Entity.DEFAULT_CHARACTER_SET + "\"";
        }
        return mimeType;
    }

    private Response wrapInResponse(Object instance) {
        if (instance instanceof Response) {
            return (Response) instance;
        }

        return ResponseBuilder.response().entity(instance).build();
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
    }

    private Function1<? super MatchFailure, Response> failure() {
        return matchFailure -> failure(matchFailure);
    }
}
