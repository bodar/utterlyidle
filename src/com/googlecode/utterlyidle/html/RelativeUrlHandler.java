package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Strings.isEmpty;
import static com.googlecode.utterlyidle.RequestBuilder.modify;

public class RelativeUrlHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private Uri currentUri = Uri.uri("/");

    public RelativeUrlHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public Response handle(Request request) throws Exception {
        RequestBuilder builder = modify(request);
        if (isEmpty(builder.uri().scheme())) {
            builder.uri(builder.uri().scheme(currentUri.scheme()));
        }
        if (isEmpty(builder.uri().authority())) {
            builder.uri(builder.uri().authority(currentUri.authority()));
        }
        if (isEmpty(builder.uri().path())) {
            if (Strings.isEmpty(builder.uri().query())) {
                builder.uri(currentUri);
            } else {
                builder.uri(currentUri.query(builder.uri().query()));
            }
        } else if (builder.uri().isRelative()) {
            String absolutePath = currentUri.mergePath(builder.uri().path()).path();
            builder.uri(builder.uri().mergePath(absolutePath));
        }
        currentUri = builder.uri();
        return httpHandler.handle(builder.build());
    }

    public Uri getCurrentUri() {
        return currentUri;
    }
}