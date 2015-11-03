package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.RecordingHttpHandler;
import org.junit.Test;

import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.comment;
import static com.googlecode.utterlyidle.cookies.CookieBuilder.modify;
import static com.googlecode.utterlyidle.cookies.CookieCutter.cookies;
import static com.googlecode.utterlyidle.cookies.CookieEncoding.BASE64_ENCODING;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class EncodingCookieHandlerTest {

    @Test
    public void decodesCookieValuesOnRequests() throws Exception {
        Cookie cookie = cookie("cookie1", "utterlyidle:v1:SMWNYiBOxZHhuIM=");
        Cookie expected = modify(cookie).value("Hōb Nőḃ").build();

        Request request = Request.get("/", HttpMessage.Builder.cookie(cookie));
        RecordingHttpHandler delegate = RecordingHttpHandler.recordingHttpHandler();
        EncodingCookieHandler handler = new EncodingCookieHandler(delegate, BASE64_ENCODING);

        handler.handle(request);
        assertThat(cookies(delegate.lastRequest()), contains(expected));
    }

    @Test
    public void encodesCookieValuesOnResponses() throws Exception {
        Cookie cookie = cookie("cookie1", "Hōb Nőḃ", comment("comment"));
        Cookie expected = modify(cookie).value("utterlyidle:v1:SMWNYiBOxZHhuIM=").build();

        Response response = response().cookie(cookie).build();
        EncodingCookieHandler handler = new EncodingCookieHandler(returns(response), BASE64_ENCODING);
        assertThat(cookies(handler.handle(anyRequest())), contains(expected));
    }

    private Request anyRequest() {
        return Request.get("/");
    }
}