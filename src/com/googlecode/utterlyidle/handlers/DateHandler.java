package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.utterlyidle.HttpHeaders.DATE;

public class DateHandler implements HttpHandler{
    private final HttpHandler httpHandler;
    private final Clock clock;

    public DateHandler(HttpHandler httpHandler, Clock clock) {
        this.httpHandler = httpHandler;
        this.clock = clock;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if(response.headers().contains(DATE)){
            return response;
        }
        return response.header(DATE, Dates.RFC822().format(clock.now()));
    }
}
