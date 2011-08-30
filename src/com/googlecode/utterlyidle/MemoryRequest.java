package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_BODY_SEPARATOR;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;
import static java.lang.String.format;

public class MemoryRequest implements Request {
    private final String method;
    private Uri uri;
    private final byte[] input;
    private final HeaderParameters headers;
    private QueryParameters query;
    private FormParameters form;
    private CookieParameters cookies;

    protected MemoryRequest(String method, Uri uri, HeaderParameters headers, byte[] input) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.input = input == null ? new byte[0] : input;
        setContentLength();
    }

    private void setContentLength() {
        if(headers().contains(CONTENT_LENGTH)){
            headers().remove(CONTENT_LENGTH);
        }
        headers().add(CONTENT_LENGTH, String.valueOf(input().length));
    }

    public String method() {
        return method;
    }

    public Uri uri() {
        return uri;
    }

    public Request uri(Uri uri) {
        this.uri = uri;
        this.query = null;
        return this;
    }

    public byte[] input() {
        return input;
    }

    public HeaderParameters headers() {
        return headers;
    }

    public QueryParameters query() {
        if (query == null) {
            query = QueryParameters.parse(uri().query());
        }
        return query;
    }

    public CookieParameters cookies() {
        if(cookies==null){
            cookies = CookieParameters.cookies(this.headers());
        }
        return cookies;
    }

    public FormParameters form() {
        if (form == null) {
            String contentType = headers().getValue(HttpHeaders.CONTENT_TYPE);
            if (contentType != null && contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED)) {
                form = FormParameters.parse(new String(input()));
            } else {
                form = FormParameters.formParameters();
            }
        }
        return form;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(format("%s %s HTTP/1.1%s", method, uri, HTTP_LINE_SEPARATOR));
        result.append(headers());
        result.append(HTTP_BODY_SEPARATOR);
        result.append(new String(input()));
        return result.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(other != null) {
            return other.toString().equals(toString());
        }
        return false;
    }
}