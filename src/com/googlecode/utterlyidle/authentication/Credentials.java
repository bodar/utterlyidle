package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.Value;

import java.util.List;

import static com.googlecode.totallylazy.Lists.list;

public abstract class Credentials implements Value<List<Credential>> {

    public static Credentials credentials(final Credential... credentials) {
        return new Credentials() {
            @Override
            public List<Credential> value() {
                return list(credentials);
            }
        };
    }
}
