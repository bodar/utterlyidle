package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.security.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static java.lang.String.format;

public class S3Signer {
    public String authorizationHeader(AwsCredentials credentials, String data) throws Exception {
        return format("AWS %s:%s", credentials.accessKeyId(), sign(credentials, data));
    }

    public String sign(final AwsCredentials credentials, final String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return Base64.encode(mac(credentials).doFinal(data.getBytes("UTF8"))).trim();
    }

    private Mac mac(AwsCredentials credentials) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] keyBytes = credentials.secretKey().getBytes("UTF8");
        final SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        final Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return mac;
    }
}
