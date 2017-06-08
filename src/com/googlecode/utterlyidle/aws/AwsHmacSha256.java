package com.googlecode.utterlyidle.aws;

import com.googlecode.totallylazy.security.Digest;
import com.googlecode.totallylazy.security.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static com.googlecode.totallylazy.Bytes.bytes;

public interface AwsHmacSha256 {
    static String hash(String payload) {
        return hash(bytes(payload));
    }

    static String hash(byte[] payload) {
        return Digest.sha256(payload).toHex();
    }

    static byte[] hmacSHA256(byte[] key, String data) {
        try {
            String algorithm = "HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(bytes(data));
        } catch (Exception e) {
            throw new RuntimeException("Could not run HMAC SHA256", e);
        }
    }

    static String hex(byte[] data) {
        return Hex.encode(data);
    }
}
