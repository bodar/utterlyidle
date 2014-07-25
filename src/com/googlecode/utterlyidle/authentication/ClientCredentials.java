package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.Value;

import java.util.Map;

public abstract class ClientCredentials implements Value<Map<String, Credential>> {
    static ClientCredentials clientCredentials(final Map<String, Credential> data) {
        return new ClientCredentials() {
            @Override
            public Map<String, Credential> value() {
                return data;
            }
        };
    }
}
