package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import org.junit.Test;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class InternalHttpHandlerTest {
    @Test
    public void shouldPassWhitelistedHeadersThrough() throws Exception {
        Cookie cookie = cookie("monster", "chocolate");
        Request cookieRequest = Request.get("/foo", HttpMessage.Builder.cookie(cookie), HttpMessage.Builder.header(AUTHORIZATION, "basic auth string"));
        Request sitemeshRequest = Request.get("/bar");
        SnoopingRequestMarker snoopingRequestMarker = new SnoopingRequestMarker(applicationId());

        new InternalHttpHandler(snoopingRequestMarker, new HelloWorldApplication(basePath("base")), cookieRequest).handle(sitemeshRequest);

        assertThat(snoopingRequestMarker.request.headers().getValue(COOKIE), is(cookie.toString()));
        assertThat(snoopingRequestMarker.request.headers().getValue(AUTHORIZATION), is("basic auth string"));
    }

    @Test
    public void shouldIgnoreCaseOfHeaders() throws Exception {
        String cookieValue = "why hello";
        Request cookieRequest = Request.get("/foo", HttpMessage.Builder.header(COOKIE.toLowerCase(), cookieValue));
        Request sitemeshRequest = Request.get("/bar");
        SnoopingRequestMarker snoopingRequestMarker = new SnoopingRequestMarker(applicationId());

        new InternalHttpHandler(snoopingRequestMarker, new HelloWorldApplication(BasePath.basePath("base")), cookieRequest).handle(sitemeshRequest);

        assertThat(snoopingRequestMarker.request.headers().getValue(COOKIE), is(cookieValue));
    }

    @Test
    public void shouldNotPassNonWhitelistedHeadersThrough() throws Exception {
        Request cookieRequest = Request.get("/foo", HttpMessage.Builder.header("X-StuffIsAwesome", true));
        Request sitemeshRequest = Request.get("/bar");
        SnoopingRequestMarker snoopingRequestMarker = new SnoopingRequestMarker(applicationId());

        new InternalHttpHandler(snoopingRequestMarker, new HelloWorldApplication(basePath("base")), cookieRequest).handle(sitemeshRequest);

        assertThat(snoopingRequestMarker.request.headers().getValue("X-StuffIsAwesome"), is(nullValue()));
    }

    private static class SnoopingRequestMarker extends InternalRequestMarker {
        public Request request;

        public SnoopingRequestMarker(ApplicationId applicationId) {
            super(applicationId);
        }

        @Override
        public Request markAsInternal(Request request) {
            this.request = request;
            return super.markAsInternal(request);
        }
    }
}
