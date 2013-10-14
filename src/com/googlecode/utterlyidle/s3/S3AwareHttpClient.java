package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.RedirectHttpHandler;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.s3.AnyS3Request.anyS3Request;
import static com.googlecode.utterlyidle.s3.AwsCredentialsRule.rule;
import static com.googlecode.utterlyidle.s3.S3RequestStringifier.s3;

public class S3AwareHttpClient implements HttpHandler {
    public static final String s3Scheme = "s3";

    private final HttpHandler httpClient;
    private final S3RequestSigner signer;

    public S3AwareHttpClient(final AwsCredentials credentials) {
        this(new RedirectHttpHandler(new ClientHttpHandler()), new S3RequestSigner(sequence(rule(anyS3Request, credentials))));
    }

    public S3AwareHttpClient(final HttpHandler httpClient, final AwsCredentials credentials) {
        this(httpClient, new S3RequestSigner(sequence(rule(anyS3Request, credentials))));
    }

    public S3AwareHttpClient(final HttpHandler httpClient, final S3RequestSigner signer) {
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
        return s3Scheme.equalsIgnoreCase(request.uri().scheme())
                ? modify(request).uri(expandS3Uri(request.uri())).build()
                : request;
    }

    private Uri expandS3Uri(final Uri uri) {
        return uri.
                scheme("https").
                authority(uri.authority() + "." + s3);
    }
}