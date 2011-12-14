package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;

import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_BODY_SEPARATOR;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;
import static java.lang.String.format;

public class MemoryRequest implements Request {
    private final String method;
    private final Uri uri;
    private final byte[] entity;
    private final HeaderParameters headers;

    public MemoryRequest(String method, Uri uri, HeaderParameters headers, byte[] entity) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.entity = entity == null ? new byte[0] : entity;
        setContentLength();
    }

    private void setContentLength() {
        if(headers().contains(CONTENT_LENGTH)){
            headers().remove(CONTENT_LENGTH);
        }
        headers().add(CONTENT_LENGTH, String.valueOf(entity().length));
    }

    public String method() {
        return method;
    }

    public Uri uri() {
        return uri;
    }

    public byte[] entity() {
        return entity;
    }

    public HeaderParameters headers() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(format("%s %s HTTP/1.1%s", method, uri, HTTP_LINE_SEPARATOR));
        result.append(headers());
        result.append(HTTP_BODY_SEPARATOR);
        result.append(new String(entity()));
        return result.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Request && other.toString().equals(toString());
    }
}