package com.googlecode.utterlyidle.aws;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.comparators.Comparators;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Request;

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
                .map(new Callable1<Pair<String, String>, String>() {
                    @Override
                    public String call(final Pair<String, String> header) throws Exception {
                        return header.first().toLowerCase();
                    }
                })
                .sortBy(Comparators.<String>ascending())
                .toString(";");
    }

    private String canonicalHeaders(Request request) {
        return sequence(request.headers())
                .map(new Callable1<Pair<String, String>, String>() {
                    @Override
                    public String call(final Pair<String, String> header) throws Exception {
                        return header.first().toLowerCase() + ":" + header.second().replaceAll("\\s+", " ").trim();
                    }
                })
                .sortBy(Comparators.<String>ascending())
                .toString("\n");
    }

    private String canonicalQueryString(Request request) {
        QueryParameters parameters = QueryParameters.parse(request.uri().query());
        return sequence(parameters)
                .map(new Callable1<Pair<String, String>, String>() {
                    @Override
                    public String call(final Pair<String, String> param) throws Exception {
                        return encode(param.getKey()) + "=" + encode(param.getValue());
                    }
                })
                .sortBy(Comparators.<String>ascending())
                .toString("&");
    }

    private String payloadHash(Request request) {
        return AwsHmacSha256.hash(request.entity().asBytes());
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
