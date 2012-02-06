package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.cookies.Cookie;

import java.util.Date;

import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;

public class MemoryResponse implements Response {
    private final HeaderParameters headers;
    private Status status;
    private byte[] bytes = new byte[0];
    private Object entity;

    public MemoryResponse(Status status) {
        this(status, headerParameters());
    }

    public MemoryResponse(Status status, HeaderParameters headerParameters) {
        this.status = status;
        this.headers = headerParameters;
    }

    public Status status() {
        return status;
    }

    public Response status(Status value) {
        this.status = value;
        return this;
    }

    public String header(String name) {
        return headers.getValue(name);
    }

    public Iterable<String> headers(String name) {
        return headers.getValues(name);
    }

    public HeaderParameters headers() {
        return headers;
    }

    public Response header(String name, Object value) {
        if (value == null) {
            return this;
        }
        if (value instanceof Date) {
            header(name, Dates.RFC822().format((Date) value));
        }
        headers.add(name, value.toString());
        return this;
    }

    public Response cookie(String name, Cookie value) {
        header(HttpHeaders.SET_COOKIE, toHttpHeader(name, value));
        return this;
    }

    public byte[] bytes() {
        return bytes;
    }

    public Response bytes(byte[] value) {
        this.bytes = value;
        return this;
    }

    public Object entity() {
        return entity;
    }

    public Response entity(Object value) {
        entity = value;
        return this;
    }

    @Override
    public String toString() {
        return String.format("HTTP/1.1 %s\r\n%s\r\n\r\n%s", status, headers, new String(bytes()));
    }

    @Override
    public int hashCode() {
        return status.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Response) {
            Response response = (Response) other;
            return status.equals(response.status()) && (entity != null ? entity.equals(response.entity()) : true) && headers.equals(response.headers());
        }
        return false;
    }
}