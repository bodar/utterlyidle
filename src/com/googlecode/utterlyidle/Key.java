package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Value;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import static com.googlecode.utterlyidle.handlers.GzipHandler.gzip;
import static com.googlecode.utterlyidle.handlers.GzipHandler.ungzip;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class Key implements Value<String> {
    public static final String ALGORITHM = "AES";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final String secret;

    private Key(String secret) {
        this.secret = secret;
    }

    public static Key key() {
        return key(generate());
    }

    public static Key key(String secret) {
        return new Key(secret);
    }

    public String encrypt(String value) {
        try {
            return string(encode(encrypt(gzip(bytes(value)))));
        } catch (Exception e) {
            throw LazyException.lazyException(e);
        }
    }

    public String decrypt(String value) {
        try {
            return string(ungzip(decrypt(decode(bytes(value)))));
        } catch (Exception e) {
            throw LazyException.lazyException(e);
        }
    }

    @Override
    public String value() {
        return secret;
    }

    private byte[] bytes(String value) {
        return value.getBytes(UTF8);
    }

    private String string(byte[] value) throws GeneralSecurityException {
        return new String(value, UTF8);
    }

    private static String generate() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            generator.init(128);
            return new String(encode(generator.generateKey().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw LazyException.lazyException(e);
        }
    }

    private static byte[] decode(byte[] content) {
        return new Base64().decode(content);
    }

    private static byte[] encode(byte[] content) {
        return new Base64().encode(content);
    }

    private byte[] encrypt(byte[] content) throws GeneralSecurityException {
        return cipher(ENCRYPT_MODE).doFinal(content);
    }

    private byte[] decrypt(byte[] content) throws GeneralSecurityException {
        return cipher(DECRYPT_MODE).doFinal(content);
    }

    private Cipher cipher(int mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, new SecretKeySpec(decode(bytes(secret)), ALGORITHM));
        return cipher;
    }

    @Override
    public String toString() {
        return value();
    }
}
