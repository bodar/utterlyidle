package com.googlecode.utterlyidle.s3;

import com.googlecode.totallylazy.io.Uri;

import static com.googlecode.totallylazy.Strings.isBlank;

public class S3 {
    public static final String dateHeader = "x-amz-date";
    public static final String baseAuthority = "s3.amazonaws.com";
    public static final String scheme = "s3";

    public static Uri rootUri() {
        return Uri.uri(scheme + "://");
    }

    public static Uri uri(String bucketName){
        return rootUri().authority(bucketName).path("/");
    }

    public static Uri uri(String bucketName, String key){
        return uri(bucketName).path("/" + key);
    }

    public static Uri toHttpUri(final Uri uri) {
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
