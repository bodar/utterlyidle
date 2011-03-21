package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.io.Url;

import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;
import static com.googlecode.utterlyidle.io.Converter.asString;
import static java.lang.String.format;

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
        this.input = input;
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

    public InputStream input() {
        return new ByteArrayInputStream(input);
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
            cookies = CookieParameters.cookies(this);
        }
        return cookies;
    }

    public FormParameters form() {
        if (form == null) {
            if ("application/x-www-form-urlencoded".equals(headers().getValue(HttpHeaders.CONTENT_TYPE))) {
                form = FormParameters.parse(asString(input()));
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
        result.append(HTTP_LINE_SEPARATOR);
        result.append(inputAsRequestString());
        return result.toString();
    }

    private String inputAsRequestString() {
        String inputAsString = asString(input());
        return inputAsString.length() == 0 ? "" : format("Content-length: %s" + HTTP_LINE_SEPARATOR + "\r\n%s", input.length, inputAsString);
    }
}