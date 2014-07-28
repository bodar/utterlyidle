package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.utterlyidle.Base64;

import static com.googlecode.totallylazy.Characters.alphaNumeric;
import static com.googlecode.totallylazy.Predicates.in;
import static com.googlecode.totallylazy.parser.Parsers.isChar;
import static com.googlecode.totallylazy.parser.Parsers.ws;

interface BasicGrammar {
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


    Parser<String> quoted = Parsers.notChar('"').many1().surroundedBy(isChar('"')).map(Parsers.toString);
    Parser<String> realm = Parsers.string("realm=").next(quoted);
    Parser<String> Challenge = Scheme.next(ws(realm));

    Function1<String, Option<String>> parseChallenge = new Function1<String, Option<String>>() {
        @Override
        public Option<String> call(final String raw) throws Exception {
            return Challenge.parse(raw).option();
        }
    };

    Function1<String, Option<Credential>> parseCredential = new Function1<String, Option<Credential>>() {
        @Override
        public Option<Credential> call(final String auth) throws Exception {
            return Credentials.parse(auth).option();
        }
    };
}
