package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.io.Url;

import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.googlecode.utterlyidle.Rfc2616.HTTP_BODY_SEPARATOR;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;
import static com.googlecode.utterlyidle.io.Converter.asString;
import static java.lang.String.format;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;

public class MemoryRequest implements Request {
    private final String method;
    private Url url;
    private final byte[] input;
    private final HeaderParameters headers;
    private QueryParameters query;
    private FormParameters form;
    private CookieParameters cookies;

    protected MemoryRequest(String method, Url url, HeaderParameters headers, byte[] input) {
        this.method = method;
        this.url = url;
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

    public Url url() {
        return url;
    }

    public Request url(Url url) {
        this.url= url;
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
            query = QueryParameters.parse(url().getQuery());
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
            if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
                form = FormParameters.parse(new String(input()));
            } else {
                form = FormParameters.formParameters();
            }
        }
        return form;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(format("%s %s HTTP/1.1%s", method, url, HTTP_LINE_SEPARATOR));
        result.append(headers());
        result.append(HTTP_BODY_SEPARATOR);
        result.append(new String(input()));
        return result.toString();
    }

}