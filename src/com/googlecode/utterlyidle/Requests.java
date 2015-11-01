package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.CookieParameters;

public class Requests {

    public static QueryParameters query(Request request) {
        return QueryParameters.parse(request.uri().query());
    }

    public static FormParameters form(Request request) {
        String contentType = request.headers().getValue(HttpHeaders.CONTENT_TYPE);
        if (contentType != null && contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            return FormParameters.parse(request.entity());
        } else {
            return FormParameters.formParameters();
        }
    }

    public static CookieParameters cookies(Request request) {
        return CookieParameters.cookies(request);
    }
}
