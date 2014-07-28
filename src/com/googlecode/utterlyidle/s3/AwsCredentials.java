package com.googlecode.utterlyidle.s3;

public class AwsCredentials {
    private final String accessKey;
    private final String secretKey;

    public AwsCredentials(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public static AwsCredentials awsCredentials(final String accessKey, final String secretKey) {
        return new AwsCredentials(accessKey, secretKey);
    }

    public String accessKeyId() {
        return accessKey;
    }

    public String secretKey() {
        return secretKey;
    }
}
