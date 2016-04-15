package com.googlecode.utterlyidle.aws;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AwsHmacSha256 {

    public static String hash(String payload) {
        return hash(payload.getBytes());
    }

    public static String hash(byte[] payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] res = digest.digest(payload);
            return hex(res);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hmacSHA256(byte[] key, String data) {
        try {
            String algorithm = "HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data.getBytes("UTF8"));
        } catch (Exception e) {
            throw new RuntimeException("Could not run HMAC SHA256", e);
        }
    }

    public static String hex(byte[] data) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : data) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString().toLowerCase();
    }
}
