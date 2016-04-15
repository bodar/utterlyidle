package com.googlecode.utterlyidle.aws;

import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.SettableClock;
import com.googlecode.utterlyidle.AuditTest;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.handlers.AuditHandler;
import com.googlecode.utterlyidle.s3.AwsCredentials;
import com.googlecode.utterlyidle.s3.EmptyResponseHandler;
import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.aws.AwsCredentialScope.awsCredentialScope;
import static com.googlecode.utterlyidle.s3.AwsCredentials.awsCredentials;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AwsHttpClientTest {

    private final AuditTest.TestAuditor auditor = new AuditTest.TestAuditor();
    private final AuditHandler auditHandler = new AuditHandler(new EmptyResponseHandler(), auditor);

    private final AwsCredentialScope scope = awsCredentialScope("us-east", "s3");
    private final AwsCredentials credentials = awsCredentials("access", "secret");

    private final SettableClock clock = new SettableClock(Dates.date(2016, 1, 27, 15, 32, 50, 27));
    private final AwsHttpClient client = new AwsHttpClient(auditHandler, clock, scope, credentials);

    @Test
    public void adds_authorization() throws Exception {
        assertThat(
                delegatedRequest(get("/test").build()).headers().getValue("Authorization"),
                is("AWS4-HMAC-SHA256 Credential=access/20160127/us-east/s3/aws4_request, SignedHeaders=content-length;x-amz-date, Signature=cfb15309d8787bd6879c2c01f805c2d6d648b3fd0719fe43647a6831fbce2774")
        );
    }

    @Test
    public void adds_time_header() throws Exception {
        assertThat(
                delegatedRequest(get("/test").build()).headers().getValue("x-amz-date"),
                is("20160127T153250Z")
        );
    }

    @Test
    public void adds_content_sha256() throws Exception {
        assertThat(
                delegatedRequest(get("/test").build()).headers().getValue("x-amz-content-sha256"),
                is("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
        );
    }

    private Request delegatedRequest(final Request request) throws Exception {
        return delegatedRequest(client, request);
    }

    private Request delegatedRequest(final AwsHttpClient client1, final Request request) throws Exception {
        client1.handle(request);
        return AuditTest.TestAuditor.receivedRequest;
    }
}