package com.googlecode.utterlyidle.s3;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.RedirectHttpHandler;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.s3.AnyS3Request.anyS3Request;
import static com.googlecode.utterlyidle.s3.AwsCredentialsRule.rule;

public class S3HttpClient implements HttpClient {

    private final HttpHandler httpClient;
    private final S3RequestSigner signer;

    public S3HttpClient(final AwsCredentials credentials) {
        this(new RedirectHttpHandler(new ClientHttpHandler()), new S3RequestSigner(sequence(rule(anyS3Request, credentials))));
    }

    public S3HttpClient(final HttpHandler httpClient, final AwsCredentials credentials) {
        this(httpClient, new S3RequestSigner(sequence(rule(anyS3Request, credentials))));
    }

    public S3HttpClient(final HttpHandler httpClient, final S3RequestSigner signer) {
        this.httpClient = httpClient;
        this.signer = signer;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return httpClient.handle(possiblySign(s3ToHttp(request)));
    }

    private Request possiblySign(final Request request) {
        return signer.sign(request);
    }

    private Request s3ToHttp(final Request request) {
        return modify(request).uri(S3.toHttpUri(request.uri())).build();
    }
}