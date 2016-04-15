package com.googlecode.utterlyidle.aws;

import static com.googlecode.utterlyidle.aws.AwsHmacSha256.hash;

public class AwsStringToSign {

    public static final String ALGORITHM = "AWS4-HMAC-SHA256";

    private String stringToSign;

    public AwsStringToSign(AwsCanonicalRequest canonicalRequest, AwsCredentialScope requestScope, AwsRequestDate date) {
        this.stringToSign = ALGORITHM +
            "\n" +
            date.full() +
            "\n" +
            requestScope.awsCredentialScope(date) +
            "\n" +
            hash(canonicalRequest.toString());
    }

    @Override
    public String toString() {
        return stringToSign;
    }

}
