package com.googlecode.utterlyidle.html;

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
        RequestBuilder requestBuilder = modify(request);
        if (currentUri != null) {
            Uri newUri = requestBuilder.uri();
            if (isEmpty(newUri.path())) {
                if (GET.equals(request.method())) {
                    requestBuilder.uri(currentUri.query(newUri.query()));
                } else {
                    requestBuilder.uri(currentUri);
                }
            } else if (newUri.isRelative()) {
                String absolutePath = currentUri.mergePath(newUri.path()).path();
                requestBuilder.uri(newUri.mergePath(absolutePath));
            }
        }
        currentUri = requestBuilder.uri();
        return httpHandler.handle(requestBuilder.build());
    }

    public Uri getCurrentUri() {
        return currentUri;
    }
}