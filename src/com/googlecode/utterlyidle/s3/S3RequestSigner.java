package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;

import static com.googlecode.totallylazy.predicates.Predicates.always;
import static com.googlecode.totallylazy.predicates.Predicates.matches;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.s3.AwsCredentialsRule.functions.credentials;
import static com.googlecode.utterlyidle.s3.AwsCredentialsRule.rule;

public class S3RequestSigner {
    private final Sequence<AwsCredentialsRule> rules;
    private final S3Signer signer;
    private final Clock clock;
    private final S3RequestStringifier stringifier;

    public S3RequestSigner(final AwsCredentials credentials, final Clock clock) {
        this(
                new S3RequestStringifier(),
                new S3Signer(),
                clock,
                sequence(rule(always(Request.class), credentials)));
    }

    public S3RequestSigner(final Iterable<AwsCredentialsRule> rules) {
        this(
                new S3RequestStringifier(),
                new S3Signer(),
                new SystemClock(),
                rules);
    }

    public S3RequestSigner(final S3RequestStringifier stringifier, final S3Signer s3Signer, final Clock clock, final Iterable<AwsCredentialsRule> rules) {
        this.stringifier = stringifier;
        this.signer = s3Signer;
        this.clock = clock;
        this.rules = sequence(rules);
    }


    public Request sign(Request request) {
        return rules.
                find(matches(request)).
                map(credentials()).
                map(actuallySign(request)).
                getOrElse(request);
    }

    private Function1<AwsCredentials, Request> actuallySign(final Request request) {
        return credentials -> {
            Request requestWithDate = ensureDate(request);
            String stringToSign = stringifier.stringToSign(requestWithDate);
            String authorisationHeader = signer.authorizationHeader(credentials, stringToSign);

            return modify(requestWithDate).
                    header(HttpHeaders.AUTHORIZATION, authorisationHeader).
                    build();
        };
    }

    private Request ensureDate(final Request request) {
        return request.headers().valueOption(DATE).isEmpty() && request.headers().valueOption(S3.dateHeader).isEmpty()
                ? addDate(request)
                : request;
    }

    private Request addDate(final Request request) {
        String currentDate = Dates.format("EEE, dd MMM yyyy HH:mm:ss Z").format(clock.now());
        return modify(request)
                .header(DATE, currentDate)
                .build();
    }
}
