package com.googlecode.utterlyidle;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class KeyTest {
    @Test
    public void canGenerateEncodedKey() throws Exception {
        isValidSecretKey(Key.key());
    }

    @Test
    public void canProvideSecretForKey() throws Exception {
        isValidSecretKey(Key.key("lpYsPLjBJnK4Up4mrgomWQ=="));
    }

    @Test
    public void canEncryptAndDecrypt() throws Exception {
        Key key = Key.key();
        String value = "Hi Mum!";
        String encrypted = key.encrypt(value);
        assertThat(encrypted, is(not(value)));
        assertThat(key.decrypt(encrypted), is(value));
    }

    private void isValidSecretKey(Key secret) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(new Base64().decode(secret.toString()), Key.ALGORITHM);
        Cipher.getInstance(Key.ALGORITHM).init(Cipher.ENCRYPT_MODE, keySpec);
    }
}
