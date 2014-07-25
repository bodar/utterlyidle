package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.utterlyidle.Base64;
import com.googlecode.utterlyidle.BaseUri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Characters.alphaNumeric;
import static com.googlecode.totallylazy.Predicates.in;
import static com.googlecode.totallylazy.parser.Parsers.isChar;
import static com.googlecode.totallylazy.parser.Parsers.ws;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.WWW_AUTHENTICATE;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.UNAUTHORIZED;
import static com.googlecode.utterlyidle.authentication.BasicHttpHandler.Grammar.parseCredential;
import static java.lang.String.format;

public class BasicHttpHandler implements HttpHandler {
    private final HttpHandler handler;
    private final BaseUri baseUri;
    private final Credentials credentials;

    public BasicHttpHandler(final HttpHandler handler, final BaseUri baseUri, final Credentials credentials) {
        this.handler = handler;
        this.baseUri = baseUri;
        this.credentials = credentials;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        if (notAuthenticated(request)) {
            return response(UNAUTHORIZED).
                    header(WWW_AUTHENTICATE, format("Basic realm=%s", baseUri.value().host())).
                    build();
        }
        return handler.handle(request);
    }

    private boolean notAuthenticated(final Request request) {
        return !request.headers().valueOption(AUTHORIZATION).
                flatMap(parseCredential).exists(in(credentials.value()));
    }

    interface Grammar {
        Parser<Void> Scheme = Parsers.string("Basic").ignore();

        Parser<Credential> BasicCookie = isChar(alphaNumeric.or(in('/', '+', '='))).many1().
                map(Parsers.toString).
                map(new Function1<String, Credential>() {
                    @Override
                    public Credential call(final String raw) throws Exception {
                        String[] pair = Strings.string(Base64.decode(raw)).split(":");
                        return Credential.credential(pair[0], pair[1]);
                    }
                });

        Parser<Credential> Credentials = Scheme.next(ws(BasicCookie));

        Function1<String, Option<Credential>> parseCredential = new Function1<String, Option<Credential>>() {
            @Override
            public Option<Credential> call(final String auth) throws Exception {
                return Credentials.parse(auth).option();
            }
        };
    }
}
