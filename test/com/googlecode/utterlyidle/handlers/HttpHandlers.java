package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Debug;
import com.googlecode.totallylazy.Streams;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Server;

public class HttpHandlers {
    public static Response handle(final RequestBuilder request, final Server server) throws Exception {
        return handle(new ClientHttpHandler(0), request, server);
    }

    public static Response handle(int timeout, final RequestBuilder request, final Server server) throws Exception {
        return handle(new ClientHttpHandler(timeout), request, server);
    }

    public static Response handle(final HttpHandler client, final RequestBuilder request, final Server server) throws Exception {
        HttpHandler urlHandler = new AuditHandler(client, new PrintAuditor(Debug.debugging() ? System.out : Streams.nullPrintStream()));
        Uri uri = request.uri();
        Uri path = server.uri().mergePath(uri.path()).query(uri.query()).fragment(uri.fragment());
        return urlHandler.handle(request.uri(path).build());
    }
}
