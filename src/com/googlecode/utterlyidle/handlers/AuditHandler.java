package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;

import java.io.OutputStream;
import java.util.Date;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;

public class AuditHandler implements HttpClient {
    private final HttpHandler handler;
    private final Auditor auditor;
    private final Clock clock;

    public AuditHandler(HttpHandler handler, Auditor auditor) {
        this(handler, auditor, new SystemClock());
    }

    public AuditHandler(HttpHandler handler, Auditor auditor, Clock clock) {
        this.handler = handler;
        this.auditor = auditor;
        this.clock = clock;
    }

    public Response handle(final Request request) throws Exception {
        final Date started = clock.now();

        final Response response = handler.handle(request);

        if(response.entity().isStreaming()) {
            final Block<OutputStream> original = response.entity().writer();
            response.entity().writer(outputStream -> auditor.audit(pair(request, started), pair(streamedResponse(outputStream, response, original), clock.now())));
            return response;
        }

        auditor.audit(pair(request, started), pair(response, clock.now()));
        return response;
    }

    private Response streamedResponse(final OutputStream outputStream, final Response response, final Block<OutputStream> writer) throws Exception {
        try {
            writer.call(outputStream);
            return modify(response).entity("Streaming succeeded").build();
        } catch (Exception e) {
            return modify(response).entity("Streaming failed:\n" + ExceptionRenderer.toString(e)).build();
        }
    }
}
