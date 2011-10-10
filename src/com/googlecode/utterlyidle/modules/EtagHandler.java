package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import static com.googlecode.utterlyidle.HttpHeaders.Content_MD5;
import static com.googlecode.utterlyidle.HttpHeaders.ETAG;
import static com.googlecode.utterlyidle.HttpHeaders.IF_NONE_MATCH;
import static com.googlecode.utterlyidle.Status.NOT_MODIFIED;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.modules.Digest.md5;

public class EtagHandler implements HttpHandler {
    private final HttpHandler httpHandler;

    public EtagHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if (!request.method().equals(GET) || !response.status().equals(Status.OK)) {
            return response;
        }

        Digest digest = md5(response.bytes());
        String etag = strongEtag(digest);
        if (etag.equals(request.headers().getValue(IF_NONE_MATCH))) {
            return response.status(NOT_MODIFIED).bytes(new byte[0]);
        }
        return response.header(ETAG, etag).header(Content_MD5, digest.asBase64());
    }

    private String strongEtag(Digest digest) {
        return "\"" + digest.asHex() + "\"";
    }

}
