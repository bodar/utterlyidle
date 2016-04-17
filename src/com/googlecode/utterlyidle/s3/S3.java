package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.aws.AwsHeaders;

import static com.googlecode.totallylazy.Strings.isBlank;

public interface S3 {
    String dateHeader = AwsHeaders.DATE;
    String baseAuthority = "s3.amazonaws.com";
    String scheme = "s3";

    static Uri rootUri() {
        return Uri.uri(scheme + "://");
    }

    static Uri uri(String bucketName){
        return rootUri().authority(bucketName).path("/");
    }

    static Uri uri(String bucketName, String key){
        return uri(bucketName).path("/" + key);
    }

    static Uri toHttpUri(final Uri uri) {
        if(!scheme.equalsIgnoreCase(uri.scheme()))
            return uri;
        return isBlank(uri.authority()) ?
                uri.
                        scheme("https").
                        authority(baseAuthority).
                        path("/")
                :
                uri.
                        scheme("https").
                        authority(uri.authority() + "." + baseAuthority);
    }
}
