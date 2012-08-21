package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;
import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class InternalHttpHandlerTest {
    @Test
    public void shouldPassWhitelistedHeadersThrough() throws Exception {
        Cookie cookie = cookie("chocolate");
        String name = "monster";
        Request cookieRequest = get("/foo").cookie(name, cookie).build();
        Request sitemeshRequest = get("/bar").build();
        SnoopingRequestMarker snoopingRequestMarker = new SnoopingRequestMarker(applicationId());

        new InternalHttpHandler(snoopingRequestMarker, new HelloWorldApplication(BasePath.basePath("base")), cookieRequest).handle(sitemeshRequest);

        assertThat(snoopingRequestMarker.request.headers().getValue(COOKIE), is(toHttpHeader(name, cookie)));
    }

    @Test
    public void shouldNotPassNonWhitelistedHeadersThrough() throws Exception {
        Request cookieRequest = get("/foo").header("X-StuffIsAwesome", true).build();
        Request sitemeshRequest = get("/bar").build();
        SnoopingRequestMarker snoopingRequestMarker = new SnoopingRequestMarker(applicationId());

        new InternalHttpHandler(snoopingRequestMarker, new HelloWorldApplication(BasePath.basePath("base")), cookieRequest).handle(sitemeshRequest);

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
