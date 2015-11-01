package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Rules;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.predicates.Predicates.any;
import static com.googlecode.totallylazy.functions.Functions.modify;

public class RoutingClient implements HttpClient {
    private final Rules<Uri, Uri> rules;
    private final HttpHandler handler;

    private RoutingClient(HttpHandler handler, Rules<Uri, Uri> rules) {
        this.handler = handler;
        this.rules = rules;
    }

    public static RoutingClient allTrafficTo(HttpHandler handler, final String destination) {
        return urlRouterClient(handler, Rules.<Uri, Uri>rules().
                addLast(any(Uri.class), authority(destination)));
    }

    public static Function1<Uri, Uri> authority(final String newAuthority) {
        return uri -> uri.authority(newAuthority);
    }

    public static RoutingClient urlRouterClient(HttpHandler handler, Rules<Uri, Uri> rules) {
        return new RoutingClient(handler, rules);
    }

    @Override
    public Response handle(Request request) throws Exception {
        Request newRequest = modify(request, Request.Builder.uri(rules.call(request.uri())));
        return handler.handle(newRequest);
    }
}
