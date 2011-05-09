package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.Cookie;

import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.googlecode.totallylazy.Bytes.write;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;

public class MemoryResponse implements Response {
    private Status status = Status.OK;
    private HeaderParameters headers = headerParameters();
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private Object entity;

    public MemoryResponse() {
    }

    public MemoryResponse(Status status, HeaderParameters headers, Object entity) {
        this.status = status;
        this.headers = headers;
        this.entity = entity;
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

    public Response header(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public Response cookie(String name, Cookie value) {
        header(HttpHeaders.SET_COOKIE, toHttpHeader(name, value));
        return this;
    }

    public OutputStream output() {
        return output;
    }

    public byte[] bytes() {
        return output.toByteArray();
    }

    public Response bytes(byte[] value) {
        this.output = write(value, new ByteArrayOutputStream());
        return this;
    }

    public Object entity() {
        return entity;
    }

    public Response entity(Object value) {
        entity = value;
        return this;
    }

    public void close() throws IOException {
        output.close();
    }

    @Override
    public String toString() {
        return String.format("HTTP/1.1 %s\r\n%s\r\n\r\n%s", status, headers, new String(bytes()));
    }
}