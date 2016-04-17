package com.googlecode.utterlyidle.aws;

import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.AuditHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.s3.AwsCredentials;

import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.HOST;

public class AwsHttpClient implements HttpClient {
    private final HttpClient handler;
    private final Clock clock;
    private AwsSignatureV4Signer signer;

    public AwsHttpClient(HttpClient client, Clock clock, AwsSignatureV4Signer signer) {
        this.handler = client;
        this.clock = clock;
        this.signer = signer;
    }

    public AwsHttpClient(final AuditHandler auditHandler, final AwsCredentialScope scope, final AwsCredentials credentials) {
        this(auditHandler, new SystemClock(), new AwsSignatureV4Signer(scope, credentials));
    }

    public Response handle(Request request) throws Exception {
        AwsRequestDate date = AwsRequestDate.awsRequestDate(clock.now());

        Request fullRequest = request.
                header(HOST, request.uri().host()).
                header(AwsHeaders.DATE, date.full());

        AwsCanonicalRequest canonicalRequest = new AwsCanonicalRequest(fullRequest);

        Request signedRequest = fullRequest.
                header(AUTHORIZATION, signer.authHeader(canonicalRequest, date)).
                header(AwsHeaders.CONTENT_SHA256, canonicalRequest.payloadHash());

        return handler.handle(signedRequest);
    }
}
