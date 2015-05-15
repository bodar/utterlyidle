package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.security.Base64;

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
        return new String(encodeHex(bytes));
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

    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase?DIGITS_LOWER:DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for(int j = 0; i < l; ++i) {
            out[j++] = toDigits[(240 & data[i]) >>> 4];
            out[j++] = toDigits[15 & data[i]];
        }

        return out;
    }
}

