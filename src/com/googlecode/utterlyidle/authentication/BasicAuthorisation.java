package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.security.Base64;
import com.googlecode.utterlyidle.Request;

import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static java.lang.String.format;

public interface BasicAuthorisation {
    static Request authorise(final Credentials credentials, final Request request) {
        return request.header(AUTHORIZATION, authorisation(credentials));
    }

    static String authorisation(final Credentials credentials) {
        return format("Basic %s", Base64.encode(bytes(credentials.username + ":" + credentials.password)));
    }
}
