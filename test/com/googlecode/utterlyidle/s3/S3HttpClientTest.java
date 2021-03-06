package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.AuditTest;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.handlers.AuditHandler;
import org.junit.Test;

import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.s3.S3SigningTest.dateInAmazonsExample;
import static com.googlecode.utterlyidle.s3.S3SigningTest.exampleCredentials;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class S3HttpClientTest {
    private final AuditTest.TestAuditor auditor = new AuditTest.TestAuditor();
    private final S3RequestSigner requestSigner = new S3RequestSigner(exampleCredentials, new StoppedClock(dateInAmazonsExample));
    private final AuditHandler auditHandler = new AuditHandler(new EmptyResponseHandler(), auditor);
    private final S3HttpClient client = new S3HttpClient(auditHandler, requestSigner);

    @Test
    public void expandsS3Urls() throws Exception {
        assertThat(
                delegatedRequest(Request.get("s3://johnsmith/photos/puppy.jpg")).uri(),
                is(uri("https://johnsmith.s3.amazonaws.com/photos/puppy.jpg")));
    }

    @Test
    public void expandsS3RootUrl() throws Exception {
        assertThat(
                delegatedRequest(Request.get("s3://")).uri(),
                is(uri("https://s3.amazonaws.com/")));
    }

    @Test
    // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4151
    public void signsS3Requests() throws Exception {
        assertThat(
                delegatedRequest(Request.get("http://johnsmith.s3.amazonaws.com/photos/puppy.jpg")).headers().getValue(AUTHORIZATION),
                is("AWS AKIAIOSFODNN7EXAMPLE:bWq2s1WEIj+Ydj0vQ697zp+IXMU="));
    }

    @Test
    public void doesNotSignNonS3UrlsByDefault() throws Exception {
        // Testing the constructor that only takes credentials
        S3HttpClient client = new S3HttpClient( auditHandler, exampleCredentials);
        assertThat(
                delegatedRequest(client, Request.get("http://google.com")).headers().getValue(AUTHORIZATION),
                is(nullValue()));
    }

    private Request delegatedRequest(final Request request) throws Exception {
        return delegatedRequest(client, request);
    }

    private Request delegatedRequest(final S3HttpClient client1, final Request request) throws Exception {
        client1.handle(request);
        return AuditTest.TestAuditor.receivedRequest;
    }

}
