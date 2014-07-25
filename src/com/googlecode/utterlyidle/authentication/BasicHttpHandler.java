package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.Characters;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.Base64;
import com.googlecode.utterlyidle.BaseUri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import static com.googlecode.totallylazy.Characters.alphaNumeric;
import static com.googlecode.totallylazy.Predicates.in;
import static com.googlecode.totallylazy.Sequences.repeat;
import static com.googlecode.totallylazy.parser.Parsers.isChar;
import static com.googlecode.totallylazy.parser.Parsers.ws;
import static com.googlecode.utterlyidle.ResponseBuilder.response;

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
        if(!request.headers().valueOption(HttpHeaders.AUTHORIZATION).
                flatMap(new Function1<String, Option<Credential>>() {
                    @Override
                    public Option<Credential> call(final String auth) throws Exception {
                        return Grammar.Credentials.parse(auth).option();
                    }
                }).exists(in(credentials.value()))) {
            return response(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=" + baseUri.value().host()).build();
        }
        return handler.handle(request);
    }

    static class Grammar {
        static final Parser<Void> Scheme = Parsers.string("Basic").ignore();
        static final Parser<Credential> BasicCookie = isChar(alphaNumeric.or(in('/', '+', '='))).many1().
                map(Parsers.toString).
                map(new Function1<String, Credential>() {
                    @Override
                    public Credential call(final String raw) throws Exception {
                        String[] pair = Strings.string(Base64.decode(raw)).split(":");
                        return Credential.credential(pair[0], pair[1]);
                    }
                });
        static final Parser<Credential> Credentials = Scheme.next(ws(BasicCookie));
    }
}
