package com.googlecode.utterlyidle.aws;

import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Request;

import java.util.Comparator;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.UrlEncodedMessage.encode;

public class AwsCanonicalRequest {
    private final String canonical;
    private final String signedHeaders;
    private final String payloadHash;

    public AwsCanonicalRequest(Request request) {
        signedHeaders = signedHeaders(request);
        payloadHash = payloadHash(request);
        canonical = request.method() +
            "\n" +
            request.uri().path() +
            "\n" +
            canonicalQueryString(request) +
            "\n" +
            canonicalHeaders(request) +
            "\n\n" +
            signedHeaders +
            "\n" +
            payloadHash;
    }

    private String signedHeaders(Request request) {
        return sequence(request.headers())
            .map(header -> header.first().toLowerCase())
            .sortBy(Comparator.naturalOrder())
            .toString(";");
    }

    private String canonicalHeaders(Request request) {
        return sequence(request.headers())
            .map(header -> pair(header.first().toLowerCase(), header.second().replaceAll("\\s+", " ").trim()))
            .map(header -> header.first() + ":" + header.second())
            .sortBy(Comparator.naturalOrder())
            .toString("\n");
    }

    private String canonicalQueryString(Request request) {
        QueryParameters parameters = QueryParameters.parse(request.uri().query());
        return sequence(parameters).map(param -> encode(param.getKey()) + "=" + encode(param.getValue()))
            .sortBy(Comparator.naturalOrder())
            .toString("&");
    }

    private String payloadHash(Request request) {
        return AwsHmacSha256.hash(request.entity().toBytes());
    }

    public String signedHeaders() {
        return signedHeaders;
    }

    public String payloadHash() {
        return payloadHash;
    }

    public String toString() {
        return canonical;
    }
}
