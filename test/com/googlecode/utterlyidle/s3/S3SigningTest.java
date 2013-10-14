package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.time.Dates;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.utterlyidle.HttpMessageParser.parseRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class S3SigningTest {

    public static final AwsCredentials exampleCredentials = new AwsCredentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
    public static final Date dateInAmazonsExample = Dates.date(2007, 3, 27, 19, 36, 42);

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4151
    public void addsNewLinesIfMd5AndContentTypeAreMissing() throws Exception {
        assertSigning(
                "GET /photos/puppy.jpg HTTP/1.1\n" +
                        "Host: johnsmith.s3.amazonaws.com\n" +
                        "Date: Tue, 27 Mar 2007 19:36:42 +0000",
                "GET\n" +
                        "\n" +
                        "\n" +
                        "Tue, 27 Mar 2007 19:36:42 +0000\n" +
                        "/johnsmith/photos/puppy.jpg",
                "AWS AKIAIOSFODNN7EXAMPLE:bWq2s1WEIj+Ydj0vQ697zp+IXMU=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4151
    public void usesAuthorityInUrlIfHostHeaderNotPresent() throws Exception {
        assertSigning(
                "GET https://johnsmith.s3.amazonaws.com:80/photos/puppy.jpg HTTP/1.1\n" +
                        "Date: Tue, 27 Mar 2007 19:36:42 +0000",
                "GET\n" +
                        "\n" +
                        "\n" +
                        "Tue, 27 Mar 2007 19:36:42 +0000\n" +
                        "/johnsmith/photos/puppy.jpg",
                "AWS AKIAIOSFODNN7EXAMPLE:bWq2s1WEIj+Ydj0vQ697zp+IXMU=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4151
    public void handlesPathStyleRequests() throws Exception {
        assertSigning(
                "GET https://s3.amazonaws.com/johnsmith/photos/puppy.jpg HTTP/1.1\n" +
                        "Date: Tue, 27 Mar 2007 19:36:42 +0000",
                "GET\n" +
                        "\n" +
                        "\n" +
                        "Tue, 27 Mar 2007 19:36:42 +0000\n" +
                        "/johnsmith/photos/puppy.jpg",
                "AWS AKIAIOSFODNN7EXAMPLE:bWq2s1WEIj+Ydj0vQ697zp+IXMU=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4183
    public void signsContentType() throws Exception {
        assertSigning(
                "PUT /photos/puppy.jpg HTTP/1.1\n" +
                        "Content-Type: image/jpeg\n" +
                        "Content-Length: 94328\n" +
                        "Host: johnsmith.s3.amazonaws.com\n" +
                        "Date: Tue, 27 Mar 2007 21:15:45 +0000",
                "PUT\n" +
                        "\n" +
                        "image/jpeg\n" +
                        "Tue, 27 Mar 2007 21:15:45 +0000\n" +
                        "/johnsmith/photos/puppy.jpg",
                "AWS AKIAIOSFODNN7EXAMPLE:MyyxeRY7whkBe+bq8fHCL/2kKUg=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4215
    public void handlesBucketListing() throws Exception {
        assertSigning(
                "GET /?prefix=photos&max-keys=50&marker=puppy HTTP/1.1\n" +
                        "User-Agent: Mozilla/5.0\n" +
                        "Host: johnsmith.s3.amazonaws.com\n" +
                        "Date: Tue, 27 Mar 2007 19:42:41 +0000",
                "GET\n" +
                        "\n" +
                        "\n" +
                        "Tue, 27 Mar 2007 19:42:41 +0000\n" +
                        "/johnsmith/",
                "AWS AKIAIOSFODNN7EXAMPLE:htDYFYduRNen8P9ZfE/s9SuKy0U=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4247
    public void handlesAccessControlListRequest() throws Exception {
        assertSigning(
                "GET /?acl HTTP/1.1\n" +
                        "Host: johnsmith.s3.amazonaws.com\n" +
                        "Date: Tue, 27 Mar 2007 19:44:46 +0000",
                "GET\n" +
                        "\n" +
                        "\n" +
                        "Tue, 27 Mar 2007 19:44:46 +0000\n" +
                        "/johnsmith/?acl",
                "AWS AKIAIOSFODNN7EXAMPLE:c2WLPFtWHVgbEmeEG93a4cG37dM=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4279
    public void defersToXAmzDateHeaderIfAvailable() throws Exception {
        assertSigning(
                "DELETE /johnsmith/photos/puppy.jpg HTTP/1.1\n" +
                        "User-Agent: dotnet\n" +
                        "Host: s3.amazonaws.com\n" +
                        "Date: Tue, 27 Mar 2007 21:20:27 +0000\n" +
                        "x-amz-date: Tue, 27 Mar 2007 21:20:26 +0000",
                "DELETE\n" +
                        "\n" +
                        "\n" +
                        "Tue, 27 Mar 2007 21:20:26 +0000\n" +
                        "/johnsmith/photos/puppy.jpg",
                "AWS AKIAIOSFODNN7EXAMPLE:lx3byBScXR6KzyMaifNkardMwNk=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4318
    public void normalisesXAmzHeaders() throws Exception {
        assertSigning(
                "PUT /db-backup.dat.gz HTTP/1.1\n" +
                        "User-Agent: curl/7.15.5\n" +
                        "Host: static.johnsmith.net:8080\n" +
                        "Date: Tue, 27 Mar 2007 21:06:08 +0000\n" +
                        "x-amz-acl: public-read\n" +
                        "content-type: application/x-download\n" +
                        "Content-MD5: 4gJE4saaMU4BqNR0kLY+lw==\n" +
                        "X-Amz-Meta-ReviewedBy: joe@johnsmith.net\n" +
                        "X-Amz-Meta-ReviewedBy: jane@johnsmith.net\n" +
                        "X-Amz-Meta-FileChecksum: 0x02661779\n" +
                        "X-Amz-Meta-ChecksumAlgorithm: crc32\n" +
                        "Content-Disposition: attachment; filename=database.dat\n" +
                        "Content-Encoding: gzip\n" +
                        "Content-Length: 5913339",
                "PUT\n" +
                        "4gJE4saaMU4BqNR0kLY+lw==\n" +
                        "application/x-download\n" +
                        "Tue, 27 Mar 2007 21:06:08 +0000\n" +
                        "x-amz-acl:public-read\n" +
                        "x-amz-meta-checksumalgorithm:crc32\n" +
                        "x-amz-meta-filechecksum:0x02661779\n" +
                        "x-amz-meta-reviewedby:joe@johnsmith.net,jane@johnsmith.net\n" +
                        "/static.johnsmith.net/db-backup.dat.gz",
                "AWS AKIAIOSFODNN7EXAMPLE:ilyl83RwaSoYIEdixDQcA4OnAnc=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4369
    public void handlesMissingBucketAndKey() throws Exception {
        assertSigning(
                "GET / HTTP/1.1\n" +
                        "Host: s3.amazonaws.com\n" +
                        "Date: Wed, 28 Mar 2007 01:29:59 +0000",
                "GET\n" +
                        "\n" +
                        "\n" +
                        "Wed, 28 Mar 2007 01:29:59 +0000\n" +
                        "/",
                "AWS AKIAIOSFODNN7EXAMPLE:qGdzdERIC03wnaRNKh6OqZehG9s=");
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4397
    public void handlesUnicodeKeys() throws Exception {
        assertSigning(
                "GET /dictionary/fran%C3%A7ais/pr%c3%a9f%c3%a8re HTTP/1.1\n" +
                        "Host: s3.amazonaws.com\n" +
                        "Date: Wed, 28 Mar 2007 01:49:49 +0000",
                "GET\n" +
                        "\n" +
                        "\n" +
                        "Wed, 28 Mar 2007 01:49:49 +0000\n" +
                        "/dictionary/fran%C3%A7ais/pr%c3%a9f%c3%a8re",
                "AWS AKIAIOSFODNN7EXAMPLE:DNEZGsoieTZ92F3bUfSPQcbGmlM=");
    }

    private void assertSigning(final String request, final String expectedStringToSign, final String expectedAuthorisationHeader) throws Exception {
        String stringToSign = stringToSign(request);
        assertThat("String to sign should match", stringToSign, is(expectedStringToSign));
        assertThat("Authorisation header should match", authorisationHeader(stringToSign), is(expectedAuthorisationHeader));
    }

    private String authorisationHeader(final String stringToSign) throws Exception {
        return new S3Signer().authorizationHeader(exampleCredentials, stringToSign);
    }

    private String stringToSign(final String request) {
        return new S3RequestStringifier().stringToSign(parseRequest(request));
    }
}
