package com.googlecode.utterlyidle.aws;

import com.googlecode.utterlyidle.s3.AwsCredentials;

import java.io.UnsupportedEncodingException;

import static com.googlecode.utterlyidle.aws.AwsHmacSha256.hex;
import static com.googlecode.utterlyidle.aws.AwsHmacSha256.hmacSHA256;

public class AwsSignatureV4Signer {
    private final AwsCredentialScope scope;
    private final AwsCredentials credentials;

    public AwsSignatureV4Signer(AwsCredentialScope scope, AwsCredentials credentials) {
        this.scope = scope;
        this.credentials = credentials;
    }

    public String sign(AwsCanonicalRequest request, AwsRequestDate date) {
        AwsStringToSign awsStringToSign = new AwsStringToSign(request, scope, date);
        byte[] signatureKey = getSignatureKey(credentials.secretKey(), date.basic(), scope.region(), scope.service());
        byte[] signature = hmacSHA256(signatureKey, awsStringToSign.toString());
        return hex(signature);
    }

    public String authHeader(AwsCanonicalRequest canonicalRequest, AwsRequestDate date) {
        String signature = sign(canonicalRequest, date);

        return String.format("%s Credential=%s/%s, SignedHeaders=%s, Signature=%s",
                AwsStringToSign.ALGORITHM,
                credentials.accessKeyId(), scope.awsCredentialScope(date),
                canonicalRequest.signedHeaders(),
                signature);
    }


    private byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) {
        try {
            byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
            byte[] kDate = hmacSHA256(kSecret, dateStamp);
            byte[] kRegion = hmacSHA256(kDate, regionName);
            byte[] kService = hmacSHA256(kRegion, serviceName);
            return hmacSHA256(kService, "aws4_request");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Could not generate signature key", e);
        }

    }

}
