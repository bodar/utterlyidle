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

    Parser<com.googlecode.utterlyidle.authentication.Credentials> BasicCookie = isChar(alphaNumeric.or(in('/', '+', '='))).many1().
            map(Parsers.toString).
            map(new Function1<String, com.googlecode.utterlyidle.authentication.Credentials>() {
                @Override
                public com.googlecode.utterlyidle.authentication.Credentials call(final String raw) throws Exception {
                    String[] pair = Strings.string(Base64.decode(raw)).split(":");
                    return com.googlecode.utterlyidle.authentication.Credentials.credential(pair[0], pair[1]);
                }
            });

    Parser<com.googlecode.utterlyidle.authentication.Credentials> Credentials = Scheme.next(ws(BasicCookie));


    Parser<String> quoted = Parsers.notChar('"').many1().surroundedBy(isChar('"')).map(Parsers.toString);
    Parser<String> realm = Parsers.string("realm=").next(quoted);
    Parser<String> Challenge = Scheme.next(ws(realm));

    Function1<String, Option<String>> parseChallenge = new Function1<String, Option<String>>() {
        @Override
        public Option<String> call(final String raw) throws Exception {
            return Challenge.parse(raw).option();
        }
    };

    Function1<String, Option<com.googlecode.utterlyidle.authentication.Credentials>> parseCredential = new Function1<String, Option<com.googlecode.utterlyidle.authentication.Credentials>>() {
        @Override
        public Option<com.googlecode.utterlyidle.authentication.Credentials> call(final String auth) throws Exception {
            return Credentials.parse(auth).option();
        }
    };
}
