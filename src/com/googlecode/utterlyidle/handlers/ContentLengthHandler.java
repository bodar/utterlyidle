package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Entity;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import static com.googlecode.totallylazy.numbers.Numbers.greaterThanOrEqualTo;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.Parameters.Builder.remove;

public class ContentLengthHandler implements HttpHandler {
    private final HttpHandler httpHandler;

    public ContentLengthHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(Request request) throws Exception {
        return setContentLength(httpHandler.handle(request));
    }

    public static Response setContentLength(Response response) {
        Status status = response.status();

        if(status.isInformational() || status.equals(Status.NO_CONTENT) || status.equals(Status.NOT_MODIFIED)) {
            return response.headers(remove(CONTENT_LENGTH)).entity(Entity.empty());
        }
        return response.entity().length().fold(response, (acc, value) ->
                acc.header(CONTENT_LENGTH, value));
    }

    public static HeaderParameters setContentLength(Entity entity, HeaderParameters headers) {
        if(headers.contains(CONTENT_LENGTH)) {
            byte[] bytes = entity.asBytes();
            headers.replace(CONTENT_LENGTH, String.valueOf(bytes.length));
        }

        if (entity.length().is(greaterThanOrEqualTo(0))) {
            return headers.replace(CONTENT_LENGTH, String.valueOf(entity.length().get()));
        }
        return headers;
    }
}