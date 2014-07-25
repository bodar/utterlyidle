package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest implements Value<byte[]> {
    private final byte[] bytes;

    private Digest(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Digest md5(byte[] bytes) {
        return new Digest(algorithm("MD5").digest(bytes));
    }

    public String asHex() {
        return new String(Hex.encodeHex(bytes));
    }

    public String asBase64() { return Base64.encode(bytes); }

    @Override
    public byte[] value() {
        return bytes;
    }

    private static MessageDigest algorithm(String md5) {
        try {
            return MessageDigest.getInstance(md5);
        } catch (NoSuchAlgorithmException e) {
            throw LazyException.lazyException(e);
        }
    }
}

