package com.googlecode.utterlyidle.aws;

import com.googlecode.utterlyidle.Request;
import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.put;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AwsCanonicalRequestTest {

    @Test
    public void transform_minimal_request() {
        final Request build = get("/test").build();

        AwsCanonicalRequest canonical = new AwsCanonicalRequest(build);

        assertThat(canonical.toString(), is("GET\n" +
                "/test\n" +
                "\n" +
                "content-length:0\n\n" +
                "content-length\n" +
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"));
    }

    @Test
    public void transform_full_request() {
        final Request build = put("/put-path")
                .query("z-param", "z value")
                .query("a param", "a value")
                .query("S param", "s value")
                .header("z-header", "  a   value  ")
                .header("a-header", "  another value  ")
                .entity("hello world")
                .build();

        AwsCanonicalRequest canonical = new AwsCanonicalRequest(build);

        assertThat(canonical.toString(), is("PUT\n" +
                "/put-path\n" +
                "S+param=s+value&a+param=a+value&z-param=z+value\n" +
                "a-header:another value\n" +
                "content-length:11\n" +
                "z-header:a value\n" +
                "\n" +
                "a-header;content-length;z-header\n" +
                "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9"));
    }
}