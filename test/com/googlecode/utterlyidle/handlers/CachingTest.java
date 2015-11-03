package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.predicates.Predicates.always;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.HttpHeaders.CACHE_CONTROL;
import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.HttpHeaders.EXPIRES;
import static com.googlecode.utterlyidle.MediaType.TEXT_CSS;
import static com.googlecode.utterlyidle.MediaType.TEXT_JAVASCRIPT;
import static com.googlecode.utterlyidle.PathMatcher.path;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.Request.post;
import static com.googlecode.utterlyidle.Response.ok;
import static com.googlecode.utterlyidle.handlers.CachePolicy.cachePolicy;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CachingTest {
    @Test
    public void setsCacheControlHeaders() throws Exception {
        HttpHandler handler = new CacheControlHandler(returnsResponse(ok().header(DATE, Dates.RFC822().format(date(2000, 1, 1)))), cachePolicy(60).add(always()));
        Response response = handler.handle(Request.get("/"));
        assertThat(response.header(CACHE_CONTROL).get(), is("public, max-age=60"));
        assertThat(response.header(EXPIRES).get(), is("Sat, 01 Jan 2000 00:01:00 GMT"));
    }

    @Test
    public void disablesCachingWhenItDoesNotMatch() throws Exception {
        HttpHandler handler = new CacheControlHandler(returnsResponse(ok()), cachePolicy(60));
        Response response = handler.handle(Request.get("/"));
        assertThat(response.header(CACHE_CONTROL).get(), is("private, must-revalidate"));
        assertThat(response.header(EXPIRES).get(), is("0"));
    }

    @Test
    public void passesExistingCacheHeaderThrough() throws Exception {
        HttpHandler handler = new CacheControlHandler(returnsResponse(ok().header(CACHE_CONTROL, "foo")), null);
        Response response = handler.handle(Request.get("/"));
        assertThat(response.header(CACHE_CONTROL).get(), is("foo"));
        assertThat(response.headers().contains(EXPIRES), is(false));

        HttpHandler handler1 = new CacheControlHandler(returnsResponse(ok().header(EXPIRES, "bar")), null);
        Response response1 = handler1.handle(Request.get("/"));
        assertThat(response1.header(EXPIRES).get(), is("bar"));
        assertThat(response1.headers().contains(CACHE_CONTROL), is(false));
    }

    @Test
    public void canControlPolicyBasedOnPath() throws Exception {
        assertThat(cachePolicy(60).add(path("foo")).matches(Pair.pair(Request.get("/foo"), ok())), is(true));
        assertThat(cachePolicy(60).add(path("bar")).matches(Pair.pair(Request.get("/foo"), ok())), is(false));
    }

    @Test
    public void canControlPolicyBasedOnContentType() throws Exception {
        assertThat(cachePolicy(60).add(contentType(TEXT_CSS).or(contentType(TEXT_JAVASCRIPT))).
                matches(Pair.pair(Request.get("/foo"), ok().contentType(TEXT_JAVASCRIPT))), is(true));
        assertThat(cachePolicy(60).add(contentType(TEXT_CSS).or(contentType(TEXT_JAVASCRIPT))).
                matches(Pair.pair(Request.get("/foo"), ok().contentType(MediaType.APPLICATION_ATOM_XML))), is(false));
    }

    @Test
    public void onlyAppliesForGetRequestsAndResponseIsOk() throws Exception {
        HttpHandler handler = new CacheControlHandler(returnsResponse(ok() .header(DATE, Dates.RFC822().format(date(2000, 1, 1)))), cachePolicy(60));
        Response response = handler.handle(Request.post("/"));
        assertThat(response.headers().contains(CACHE_CONTROL), is(false));
        assertThat(response.headers().contains(EXPIRES), is(false));
    }
}
