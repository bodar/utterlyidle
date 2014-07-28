package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.Request;

import static com.googlecode.totallylazy.Predicates.nullValue;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.endsWith;
import static com.googlecode.totallylazy.Strings.toLowerCase;
import static com.googlecode.utterlyidle.s3.S3RequestStringifier.hostHeaderAuthority;

public class AnyS3Request extends LogicalPredicate<Request> {
    public static final LogicalPredicate<Request> anyS3Request = new AnyS3Request();
    private static final LogicalPredicate<String> endsWithS3 = endsWith(S3.baseAuthority);

    @Override
    public boolean matches(final Request request) {
        Sequence<String> possibleAuthorityLocations = sequence(
                request.uri().authority(),
                hostHeaderAuthority(request));

        Sequence<String> s3Authorities = possibleAuthorityLocations
                .filter(Predicates.not(nullValue(String.class)))
                .map(toLowerCase())
                .filter(endsWithS3);

        return !s3Authorities.isEmpty();
    }
}
