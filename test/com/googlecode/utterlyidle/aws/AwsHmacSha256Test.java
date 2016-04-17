package com.googlecode.utterlyidle.aws;

import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.aws.AwsHmacSha256.hash;
import static com.googlecode.utterlyidle.aws.AwsHmacSha256.hex;
import static com.googlecode.utterlyidle.aws.AwsHmacSha256.hmacSHA256;
import static org.junit.Assert.assertThat;

public class AwsHmacSha256Test {
    @Test
    public void hash_content() {
        assertThat(hash("test string"), is("d5579c46dfcc7f18207013e65b44e4cb4e2c2298f4ac457ba8f82743f31e930b"));
    }

    @Test
    public void hex_content() {
        assertThat(hex("test string".getBytes()), is("7465737420737472696e67"));
    }

    @Test
    public void encrypt_content() {
        assertThat(hex(hmacSHA256("test key".getBytes(), "test string")), is("6864a9fdc9bc77190c4bc6d1d875a0afe19461907f486f4ba5213a1f15b71cc9"));
    }
}