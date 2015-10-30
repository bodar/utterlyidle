package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;

public interface HttpClient extends HttpHandler {
    class methods {
        private methods() {}

        public static HttpClient httpClient(final HttpHandler httpHandler) {
            return httpHandler::handle;
        }

    }
}
