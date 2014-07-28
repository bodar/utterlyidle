package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.Eq;
import com.googlecode.totallylazy.annotations.multimethod;

public class Credential extends Eq {
    public final String username;
    public final String password;

    public Credential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Credential credential(final String username, final String password) {
        return new Credential(username, password);
    }

    @multimethod
    public boolean equals(Credential other) { return username.equals(other.username) && password.equals(other.password); }

    @Override
    public int hashCode() { return username.hashCode() * password.hashCode() * 31; }
}
