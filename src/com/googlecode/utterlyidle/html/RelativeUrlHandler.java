package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Strings.isEmpty;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;

public class RelativeUrlHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private Uri currentUri;

    public RelativeUrlHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public Response handle(Request request) throws Exception {
        RequestBuilder builder = modify(request);
        if (currentUri != null) {
            Uri newUri = builder.uri();
            if (isEmpty(newUri.path())) {
                if (Strings.isEmpty(newUri.query())) {
                    builder.uri(currentUri);
                } else {
                    builder.uri(currentUri.query(newUri.query()));
                }
            } else if (newUri.isRelative()) {
                String absolutePath = currentUri.mergePath(newUri.path()).path();
                builder.uri(newUri.mergePath(absolutePath));
            }
        }
        currentUri = builder.uri();
        return httpHandler.handle(builder.build());
    }

    public Uri getCurrentUri() {
        return currentUri;
    }
}