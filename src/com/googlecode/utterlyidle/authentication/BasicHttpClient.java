package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Base64;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.WWW_AUTHENTICATE;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.Status.UNAUTHORIZED;
import static java.lang.String.format;

public class BasicHttpClient implements HttpClient {
    private final HttpClient client;
    private final ClientCredentials credentials;

    public BasicHttpClient(final HttpClient client, final ClientCredentials credentials) {
        this.credentials = credentials;
        this.client = client;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        Response response = client.handle(request);
        if (response.status().equals(UNAUTHORIZED)) {
            return response.headers().valueOption(WWW_AUTHENTICATE).
                    flatMap(BasicGrammar.parseChallenge).
                    flatMap(new Function1<String, Option<Response>>() {
                        @Override
                        public Option<Response> call(final String realm) throws Exception {
                            return Maps.get(credentials.value(), realm).
                                    map(new Function1<Credential, Response>() {
                                        @Override
                                        public Response call(final Credential credential) throws Exception {
                                            // how/where should we construct the encoded username/password?
                                            String authorizationHeader = format("Basic %s", Base64.encode(bytes(credential.username + ":" + credential.password)));
                                            return client.handle(modify(request).header(AUTHORIZATION, authorizationHeader).build());
                                        }
                                    });
                        }
                    }).
                    getOrElse(response);
        }
        return response;
    }
}
