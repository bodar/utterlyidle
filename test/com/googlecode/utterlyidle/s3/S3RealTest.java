package com.googlecode.utterlyidle.s3;

import com.googlecode.utterlyidle.handlers.AuditHandler;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.FullRequestPrintAuditor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.RequestBuilder.delete;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.put;
import static com.googlecode.utterlyidle.Status.NO_CONTENT;
import static com.googlecode.utterlyidle.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeTrue;

public class S3RealTest {
    private S3AwareHttpClient client;
    private Properties properties;
    private String bucketName;
    private String keyPath;
    private String key;
    private String bucketPath;

    @BeforeClass
    public static void checkPropertiesExist(){
        assumeTrue(properties() != null);
    }

    @Before
    public void createClient() throws IOException {
        properties = new Properties();
        properties.load(properties());

        client = new S3AwareHttpClient(
                new AuditHandler(new ClientHttpHandler(), new FullRequestPrintAuditor()),
                new AwsCredentials(properties.getProperty("accessKey"), properties.getProperty("secretKey")));

        bucketName = UUID.randomUUID().toString();
        key = UUID.randomUUID().toString();
        bucketPath = "s3://" + bucketName + "/";
        keyPath = bucketPath + key;
    }

    @After
    public void removeBucket() throws Exception {
        client.handle(delete(bucketPath).build());
    }

    @Test
    public void putThenGetThenDelete() throws Exception {
        String contents = UUID.randomUUID().toString();

        assertThat(
                "Bucket should not exist in root listing",
                client.handle(get("s3://").build()).entity().toString(),
                not(containsString(bucketName)));
        assertThat(
                "Put of bucket should succeed",
                client.handle(put(bucketPath).build()).status(),
                is(OK));
        assertThat(
                "Bucket should exist in root listing",
                client.handle(get("s3://").build()).entity().toString(),
                containsString(bucketName));
        assertThat(
                "Key should not exist in bucket listing",
                client.handle(get(bucketPath).build()).entity().toString(),
                not(containsString(key)));
        assertThat(
                "Put of key should succeed",
                client.handle(put(keyPath).entity(contents).build()).status(),
                is(OK));
        assertThat(
                "Key should appear in bucket listing",
                client.handle(get(bucketPath).build()).entity().toString(),
                containsString(key));
        assertThat(
                "Key contents should be as expected",
                client.handle(get(keyPath).build()).entity().toString(),
                is(contents));
        assertThat(
                "Delete of key should succeed",
                client.handle(delete(keyPath).build()).status(),
                is(NO_CONTENT));
        assertThat(
                "Key should no longer appear in bucket listing",
                client.handle(get(bucketPath).build()).entity().toString(),
                not(containsString(key)));
        assertThat(
                "Delete of bucket should succeed",
                client.handle(delete(bucketPath).build()).status(),
                is(NO_CONTENT));
        assertThat(
                "Bucket should no longer exist in root listing",
                client.handle(get("s3://").build()).entity().toString(),
                not(containsString(bucketName)));
    }

    private static InputStream properties() {
        return S3RealTest.class.getResourceAsStream("s3.properties");
    }
}
