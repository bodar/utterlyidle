package com.googlecode.utterlyidle.aws;


import static java.lang.String.format;

public class AwsCredentialScope {
    private final String service;
    private final String region;

    private AwsCredentialScope(String region, String service) {
        this.service = service;
        this.region = region;
    }

    public static AwsCredentialScope awsCredentialScope(String region, String service) {
        return new AwsCredentialScope(region, service);
    }

    public String service() {
        return service;
    }

    public String region() {
        return region;
    }

    public String awsCredentialScope(AwsRequestDate date) {
        return format("%s/%s/%s/aws4_request",
                date.basic(),
                region,
                service);
    }
}
