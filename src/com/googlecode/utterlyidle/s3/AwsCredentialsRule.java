package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.utterlyidle.Request;

public class AwsCredentialsRule implements Predicate<Request>{
    private final Predicate<? super Request> predicate;
    private final AwsCredentials credentials;

    public static AwsCredentialsRule rule(final Predicate<? super Request> predicate, final AwsCredentials credentials) {
        return new AwsCredentialsRule(credentials, predicate);
    }

    public AwsCredentialsRule(final AwsCredentials credentials, final Predicate<? super Request> predicate) {
        this.credentials = credentials;
        this.predicate = predicate;
    }

    public AwsCredentials credentials() {
        return credentials;
    }

    @Override
    public boolean matches(final Request other) {
        return predicate.matches(other);
    }

    public static class functions{
        public static Function1<AwsCredentialsRule, AwsCredentials> credentials(){
            return awsCredentialsRule -> awsCredentialsRule.credentials();
        }
    }
}
