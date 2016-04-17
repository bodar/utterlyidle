package com.googlecode.utterlyidle.aws;

import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.aws.AwsCredentialScope.awsCredentialScope;
import static com.googlecode.utterlyidle.aws.AwsRequestDate.awsRequestDate;
import static org.junit.Assert.assertThat;

public class AwsStringToSignTest {

    @Test
    public void create_from_request() {
        AwsCanonicalRequest request = new AwsCanonicalRequest(get("/test"));
        AwsRequestDate date = awsRequestDate(date(2016, 1, 27, 15, 32, 50, 27));

        AwsStringToSign stringToSign = new AwsStringToSign(request, awsCredentialScope("us-east", "s3"), date);

        assertThat(stringToSign.toString(), is("AWS4-HMAC-SHA256\n" +
                "20160127T153250Z\n" +
                "20160127/us-east/s3/aws4_request\n" +
                AwsHmacSha256.hash(request.toString())));
    }
}