package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Group;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.UrlEncodedMessage;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Pair.functions.pairToString;
import static com.googlecode.totallylazy.Pair.functions.replaceFirst;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.in;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.nullValue;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.blank;
import static com.googlecode.totallylazy.Strings.isBlank;
import static com.googlecode.totallylazy.Strings.startsWith;
import static com.googlecode.totallylazy.Strings.toLowerCase;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.HttpHeaders.Content_MD5;
import static com.googlecode.utterlyidle.HttpHeaders.HOST;
import static java.lang.String.format;
import static java.util.regex.Pattern.quote;

public class S3RequestStringifier {
    // see http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e3773
    public static final Sequence<String> canonicalResourceQueryParams = sequence("acl", "lifecycle", "location", "logging", "notification", "partNumber", "policy", "requestPayment", "torrent", "uploadId", "uploads", "versionId", "versioning", "versions", "website");

    public String stringToSign(Request request) {
        return format("%s\n%s\n%s\n%s\n%s%s",
                request.method(),
                request.headers().valueOption(Content_MD5).getOrElse(""),
                request.headers().valueOption(CONTENT_TYPE).getOrElse(""),
                date(request),
                canonicalizedAmzHeaders(request),
                canonicalizedResource(request));
    }

    private String canonicalizedAmzHeaders(final Request request) {
        String result = sequence(request.headers()).
                map(replaceFirst(toLowerCase(), String.class)).
                filter(where(first(String.class), startsWith("x-amz-"))).
                filter(where(first(String.class), not(S3.dateHeader))).
                groupBy(first(String.class)).
                map(mergeHeaders()).
                sortBy(first(String.class)).
                map(pairToString("", ":", "")).
                toString("", "\n", "");
        return isBlank(result)
                ? result
                : result + "\n";
    }

    private Function1<Group<String, Pair<String, String>>, Pair<String, String>> mergeHeaders() {
        return headers -> {
            String commaSeparatedValues = headers.map(second(String.class)).toString(",");
            return pair(headers.key(), commaSeparatedValues);
        };
    }

    private String date(Request request) {
        return sequence(
                request.headers().getValue(S3.dateHeader),
                request.headers().getValue(HttpHeaders.DATE))
                .find(not(nullValue()))
                .getOrThrow(new RuntimeException(format("No Date or %s header in request:\n%s", S3.dateHeader, request)));
    }

    private String canonicalizedResource(Request request) {
        // See http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html
        return sequence(
                virtualHostBucket(request),
                request.uri().path(),
                subresource(request)
        ).toString("");
    }

    private String subresource(final Request request) {
        String result = UrlEncodedMessage.toString(
                sequence(UrlEncodedMessage.parse(request.uri().query()))
                        .filter(where(first(String.class), in(canonicalResourceQueryParams)))
                        .sortBy(first(String.class)));
        return result.isEmpty() ? "" : "?" + result;
    }

    private String virtualHostBucket(final Request request) {
        Option<String> bucket = sequence(
                request.uri().authority(),
                hostHeaderAuthority(request))
                .find(not(S3.baseAuthority).and(not(blank())));
        return bucket.isEmpty()
                ? ""
                : "/" + bucket.get().split(quote("." + S3.baseAuthority))[0];
    }

    public static String hostHeaderAuthority(final Request request) {
        Option<String> header = request.headers().valueOption(HOST);
        return header.isEmpty()
                ? null
                : header.get().split(":")[0];
    }
}
