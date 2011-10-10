package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.HttpMethod;

import java.util.Date;

import static com.googlecode.totallylazy.time.Dates.RFC822;
import static com.googlecode.totallylazy.time.Dates.addSeconds;
import static com.googlecode.utterlyidle.HttpHeaders.CACHE_CONTROL;
import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.HttpHeaders.EXPIRES;
import static java.lang.String.format;

public class CacheControlHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final CachePolicy cachePolicy;

    public CacheControlHandler(HttpHandler httpHandler, CachePolicy cachePolicy) {
        this.httpHandler = httpHandler;
        this.cachePolicy = cachePolicy;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if(!request.method().equals(HttpMethod.GET) || !response.status().equals(Status.OK) || response.headers().contains(CACHE_CONTROL) || response.headers().contains(EXPIRES)){
            return response;
        }

        if (!cachePolicy.matches(Pair.pair(request, response))) {
            return response.
                    header(CACHE_CONTROL, "private, must-revalidate").
                    header(EXPIRES, "0");
        }

        Date now = Dates.RFC822().parse(response.header(DATE));
        return response.
                header(CACHE_CONTROL, format("public, max-age=%s", cachePolicy.value())).
                header(EXPIRES, RFC822().format(addSeconds(now, cachePolicy.value())));
    }
}
