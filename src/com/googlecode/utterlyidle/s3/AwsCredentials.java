package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.Strings;

import java.util.Map;

public class AwsCredentials {
    public static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    public static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    private final String accessKey;
    private final String secretKey;

    public AwsCredentials(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public static AwsCredentials awsCredentials(final String accessKey, final String secretKey) {
        return new AwsCredentials(accessKey, secretKey);
    }

    public static AwsCredentials awsCredentials(Map<String, String> map) {
        return awsCredentials(get(map, AWS_ACCESS_KEY_ID), get(map, AWS_SECRET_ACCESS_KEY));
    }

    private static String get(Map<String, String> map, String key) {
        String value = map.get(key);
        if(Strings.isEmpty(value)) throw new IllegalArgumentException(key + " was not set");
        return value;
    }

    public static AwsCredentials awsCredentials() {
        return awsCredentials(System.getenv());
    }

    public String accessKeyId() {
        return accessKey;
    }

    public String secretKey() {
        return secretKey;
    }
}
