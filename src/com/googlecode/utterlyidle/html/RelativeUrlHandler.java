package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Strings.isEmpty;
import static com.googlecode.utterlyidle.Request.Builder.modify;

public class RelativeUrlHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private Uri currentUri = Uri.uri("/");

    public RelativeUrlHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public Response handle(Request request) throws Exception {
        Uri result = calculateUrl(request.uri());
        currentUri = result;
        return httpHandler.handle(modify(request, Request.Builder.uri(result)));
    }

    public Uri calculateUrl(Uri uri) {
        Uri result = uri;
        if (isEmpty(result.scheme())) {
            result = result.scheme(currentUri.scheme());
        }
        if (isEmpty(result.authority())) {
            result = result.authority(currentUri.authority());
        }
        if (isEmpty(result.path())) {
            if (Strings.isEmpty(result.query())) {
                result = currentUri;
            } else {
                result = currentUri.query(result.query());
            }
        } else if (result.isRelative()) {
            String absolutePath = currentUri.mergePath(result.path()).path();
            result = result.mergePath(absolutePath);
        }
        return result;
    }

    public Uri getCurrentUri() {
        return currentUri;
    }
}