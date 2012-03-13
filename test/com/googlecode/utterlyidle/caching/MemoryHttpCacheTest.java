package com.googlecode.utterlyidle.caching;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.utterlyidle.HttpHeaders.CACHE_CONTROL;
import static com.googlecode.utterlyidle.RequestBuilder.delete;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.RequestBuilder.put;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MemoryHttpCacheTest {
    @Test
    public void doesNotNullPointerUpWhenNoCacheHeader() throws Exception {
        HttpCache cache = new MemoryHttpCache();
        assertThat(cache.isCacheable(get("/foo").build(), response(OK)), is(false));
    }

    @Test
    public void canGetAndPut() throws Exception {
        HttpCache cache = new MemoryHttpCache();
        Request request = get("/foo").build();
        assertThat(cache.get(request), is(none(Response.class)));

        Response response = response(OK).header(CACHE_CONTROL, "public, max-age=60").entity("text");
        cache.put(request, response);

        assertThat(cache.get(request), is(some(response)));

    }

    @Test
    public void canCacheGetWhenResponseStatusIsOkAndCacheHeaderAllows() throws Exception {
        HttpCache cache = new MemoryHttpCache();
        assertThat(cache.isCacheable(get("/foo").build(), response(OK).header(CACHE_CONTROL, "public, max-age=60")), is(true));
    }

    @Test
    public void doesNotCacheNonGetRequests() throws Exception {
        HttpCache cache = new MemoryHttpCache();
        assertThat(cache.isCacheable(post("/foo").build(), null), is(false));
        assertThat(cache.isCacheable(delete("/foo").build(), null), is(false));
        assertThat(cache.isCacheable(put("/foo").build(), null), is(false));
    }
}
