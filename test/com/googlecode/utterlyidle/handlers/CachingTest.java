package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Responses;
import com.googlecode.utterlyidle.Status;
import org.junit.Test;

import static com.googlecode.totallylazy.Predicates.never;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.HttpHeaders.CACHE_CONTROL;
import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.HttpHeaders.EXPIRES;
import static com.googlecode.utterlyidle.MediaType.TEXT_CSS;
import static com.googlecode.utterlyidle.MediaType.TEXT_JAVASCRIPT;
import static com.googlecode.utterlyidle.PathMatcher.path;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.handlers.CachePolicy.cachePolicy;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CachingTest {
    @Test
    public void setsCacheControlHeaders() throws Exception {
        HttpHandler handler = new CacheControlHandler(returnsResponse(response().header(DATE, Dates.RFC822().format(date(2000, 1, 1)))), cachePolicy(60));
        Response response = handler.handle(get("/").build());
        assertThat(response.header(CACHE_CONTROL), is("public, max-age=60"));
        assertThat(response.header(EXPIRES), is("Sat, 01 Jan 2000 00:01:00 UTC"));
    }

    @Test
    public void disablesCachingWhenItDoesNotMatch() throws Exception {
        HttpHandler handler = new CacheControlHandler(returnsResponse(response()), cachePolicy(60, never()));
        Response response = handler.handle(get("/").build());
        assertThat(response.header(CACHE_CONTROL), is("private, must-revalidate"));
        assertThat(response.header(EXPIRES), is("0"));
    }

    @Test
    public void passesExistingCacheHeaderThrough() throws Exception {
        HttpHandler handler = new CacheControlHandler(returnsResponse(response().header(CACHE_CONTROL, "foo")), null);
        Response response = handler.handle(get("/").build());
        assertThat(response.header(CACHE_CONTROL), is("foo"));
        assertThat(response.headers().contains(EXPIRES), is(false));

        HttpHandler handler1 = new CacheControlHandler(returnsResponse(response().header(EXPIRES, "bar")), null);
        Response response1 = handler1.handle(get("/").build());
        assertThat(response1.header(EXPIRES), is("bar"));
        assertThat(response1.headers().contains(CACHE_CONTROL), is(false));
    }

    @Test
    public void canControlPolicyBasedOnPath() throws Exception {
        assertThat(cachePolicy(60, path("foo")).matches(Pair.pair(get("/foo").build(), Responses.response())), is(true));
        assertThat(cachePolicy(60, path("bar")).matches(Pair.pair(get("/foo").build(), Responses.response())), is(false));
    }

    @Test
    public void canControlPolicyBasedOnContentType() throws Exception {
        assertThat(cachePolicy(60, contentType(TEXT_CSS).or(contentType(TEXT_JAVASCRIPT))).
                matches(Pair.pair(get("/foo").build(), response().header(HttpHeaders.CONTENT_TYPE, TEXT_JAVASCRIPT))), is(true));
        assertThat(cachePolicy(60, contentType(TEXT_CSS).or(contentType(TEXT_JAVASCRIPT))).
                matches(Pair.pair(get("/foo").build(), response().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_ATOM_XML))), is(false));
    }

    @Test
    public void onlyAppliesForGetRequestsAndResponseIsOk() throws Exception {
        HttpHandler handler = new CacheControlHandler(returnsResponse(response(Status.OK).header(DATE, Dates.RFC822().format(date(2000, 1, 1)))), cachePolicy(60));
        Response response = handler.handle(post("/").build());
        assertThat(response.headers().contains(CACHE_CONTROL), is(false));
        assertThat(response.headers().contains(EXPIRES), is(false));
    }
}
