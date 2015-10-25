package com.googlecode.utterlyidle.ssl;

import java.util.Arrays;

public class SecureString implements AutoCloseable, CharSequence {
    private final char[] value;

    protected SecureString(final char[] value) {
        this.value = value;
    }

    public static SecureString secureString(final char... value) {
        return new SecureString(value);
    }

    @Override
    public void close() throws Exception {
        int length = value.length;
        for (int i = 0; i < length; i++) value[i] = 0;
    }

    public char[] characters() {
        return value;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public char charAt(final int index) {
        return value[index];
    }

    @Override
    public SecureString subSequence(final int start, final int end) {
        return secureString(Arrays.copyOfRange(value, start, end));
    }

    @Override
    public String toString() {
        return "***********";
    }
}
