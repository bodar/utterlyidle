package com.googlecode.utterlyidle.aws;

import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.AuditHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.s3.AwsCredentials;

import static com.googlecode.totallylazy.functions.Functions.modify;

public class AwsHttpClient implements HttpClient {

    private final HttpClient handler;
    private final Clock clock;
    private final AwsCredentialScope scope;
    private final AwsCredentials credentials;

    public AwsHttpClient(HttpClient client, Clock clock, AwsCredentialScope scope, AwsCredentials credentials) {
        this.handler = client;
        this.clock = clock;
        this.scope = scope;
        this.credentials = credentials;
    }

    public AwsHttpClient(final AuditHandler auditHandler, final AwsCredentialScope scope, final AwsCredentials credentials) {
        this(auditHandler, new SystemClock(), scope, credentials);
    }

    public Response handle(Request request) throws Exception {
        AwsRequestDate date = AwsRequestDate.awsRequestDate(clock.now());

        Request fullRequest = request.
                header("host", request.uri().host()).
                header("x-amz-date", date.full());

        AwsCanonicalRequest canonicalRequest = new AwsCanonicalRequest(fullRequest);

        Request signedRequest = fullRequest.
                header("Authorization", buildAuthHeader(canonicalRequest, date)).
                header("x-amz-content-sha256", canonicalRequest.payloadHash());

        return handler.handle(signedRequest);
    }

    private String buildAuthHeader(AwsCanonicalRequest canonicalRequest, AwsRequestDate date) {
        AwsSignatureV4Signer signer = new AwsSignatureV4Signer();

        String signature = signer.sign(canonicalRequest, scope, credentials, date);

        return String.format("%s Credential=%s/%s, SignedHeaders=%s, Signature=%s",
                AwsStringToSign.ALGORITHM,
                credentials.accessKeyId(), scope.awsCredentialScope(date),
                canonicalRequest.signedHeaders(),
                signature);
    }
}
