package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public interface HttpClient extends HttpHandler {
    public static class methods {
        private methods() {}

        public static HttpClient httpClient(final HttpHandler httpHandler) {
            return new HttpClient() {
                @Override
                public Response handle(Request request) throws Exception {
                    return httpHandler.handle(request);
                }
            };
        }

    }
}
