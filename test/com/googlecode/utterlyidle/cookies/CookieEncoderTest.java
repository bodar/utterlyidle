package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.RecordingHttpHandler;
import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.comment;
import static com.googlecode.utterlyidle.cookies.CookieBuilder.*;
import static com.googlecode.utterlyidle.cookies.CookieCutter.cookies;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class CookieEncoderTest {

    @Test
    public void decodesCookieValuesOnRequests() throws Exception {
        Cookie cookie = cookie("cookie1", "SMWNYiBOxZHhuIM=");
        Cookie expected = modify(cookie).value("Hōb Nőḃ").build();

        Request request = get("/").cookie(cookie).build();
        RecordingHttpHandler delegate = RecordingHttpHandler.recordingHttpHandler();
        CookieEncoder encoder = new CookieEncoder(delegate, new Base64Encoding());

        encoder.handle(request);
        assertThat(cookies(delegate.lastRequest()), contains(expected));
    }

    @Test
    public void encodesCookieValuesOnResponses() throws Exception {
        Cookie cookie = cookie("cookie1", "Hōb Nőḃ", comment("comment"));
        Cookie expected = modify(cookie).value("SMWNYiBOxZHhuIM=").build();

        Response response = response().cookie(cookie).build();
        CookieEncoder encoder = new CookieEncoder(returns(response), new Base64Encoding());
        assertThat(cookies(encoder.handle(anyRequest())), contains(expected));
    }

    private Request anyRequest() {
        return get("/").build();
    }
}