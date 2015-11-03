package com.googlecode.utterlyidle.httpserver;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Entity;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.Entity.empty;
import static com.googlecode.utterlyidle.Entity.entity;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.httpserver.ContentLength.NoContent;
import static com.googlecode.utterlyidle.httpserver.ContentLength.Streaming;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;

public class ContentLengthTest {
    private final List<Pair<String, String>> EMPTY_HEADERS = Collections.emptyList();

    @Test
    public void noContentWhenContentLengthIsZero() {
        Response emptyResponse = Response.response(OK, singleton(pair(HttpHeaders.CONTENT_LENGTH, "0")), entity(empty()));

        assertEquals(NoContent.value(), ContentLength.handle(emptyResponse).value());
    }

    @Test
    public void streamingWhenStreaming() throws Exception {
        Entity streamingEntity = entity(new ByteArrayInputStream("balh".getBytes()));

        Response streamingResponse = Response.response(OK, EMPTY_HEADERS, entity(streamingEntity));

        assertEquals(Streaming.value(), ContentLength.handle(streamingResponse).value());
    }

    @Test
    public void noContentWhenNoContentLengthHeader() throws Exception {
        Response emptyResponse = Response.response(OK, EMPTY_HEADERS, entity(empty()));

        assertEquals(NoContent.value(), ContentLength.handle(emptyResponse).value());
    }

    @Test
    public void contentWhenContentLengthIsNotZero() throws Exception {
        Response emptyResponse = Response.response(OK, singleton(pair(HttpHeaders.CONTENT_LENGTH, "55")), entity(empty()));

        assertEquals((Long) 55L, ContentLength.handle(emptyResponse).value());
    }
}