package com.googlecode.utterlyidle.ssl;

import org.junit.Test;

import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.utterlyidle.ssl.SecureString.secureString;

public class SecureStringTest {
    @Test
    public void supportsSecureWipingOfValue() throws Exception {
        char[] password = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        try(SecureString secureString = secureString(password)) {
            for (int i = 0; i < password.length; i++) assertThat(secureString.charAt(i), is(password[i]));
        }
        for (final char c : password) assertThat(c, is((char) 0));
    }
}