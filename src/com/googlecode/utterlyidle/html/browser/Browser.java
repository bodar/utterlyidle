package com.googlecode.utterlyidle.html.browser;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.html.Html;
import com.googlecode.utterlyidle.html.RelativeUrlHandler;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.HttpMessage.Builder.cookie;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.MediaType.TEXT_HTML;
import static com.googlecode.utterlyidle.Status.FOUND;
import static com.googlecode.utterlyidle.Status.MOVED_PERMANENTLY;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.Status.TEMPORARY_REDIRECT;
import static com.googlecode.utterlyidle.html.browser.BrowserCookies.constructors.browserCookies;
import static com.googlecode.utterlyidle.html.browser.BrowserState.constructors.browserState;

public class Browser implements HttpClient, BrowserCookies<Browser>, BrowserState {
    private final HttpHandler handler;
    private final BrowserCookies<BrowserCookies> cookies = browserCookies();
    private String acceptHeader = TEXT_HTML;
    private BrowserState state = BrowserState.constructors.empty();

    public Browser(HttpHandler handler) {
        this.handler = new RelativeUrlHandler(handler);
    }

    @Override
    public Response handle(Request request) throws Exception {
        Request requestWithCurrentState =
                setCookies(ensureAcceptHeader(request));

        Response response = handler.handle(requestWithCurrentState);

        update(requestWithCurrentState, response);

        if (isRedirect(response)) {
            return handle(Request.get(response.header(LOCATION).get()));
        } else{
            return response;
        }
    }

    private boolean isRedirect(Response response) {
        return sequence(MOVED_PERMANENTLY, TEMPORARY_REDIRECT, FOUND, SEE_OTHER).contains(response.status());
    }

    private Request setCookies(Request request) {
        return modify(request, cookie(cookies.cookies()));
    }

    private Request ensureAcceptHeader(Request request) {
        if(request.headers().contains(ACCEPT)) {
            return request;
        }
        return modify(request, header(ACCEPT, acceptHeader));
    }

    private void update(Request request, final Response response) {
        state = browserState(request, response);

        for (Pair<String, String> cookie : CookieParameters.cookies(response)) {
            this.cookies.setCookie(cookie.first(), cookie.second());
        }
    }

    public String acceptHeader(){
        return acceptHeader;
    }

    public Browser acceptHeader(String value){
        acceptHeader= value;
        return this;
    }

    @Override
    public Html html() throws Exception {
        return state.html();
    }

    @Override
    public Option<Html> htmlOption() {
        return state.htmlOption();
    }

    @Override
    public Uri uri() {
        return state.uri();
    }

    @Override
    public Status status() {
        return state.status();
    }

    @Override
    public Response lastResponse() {
        return state.lastResponse();
    }

    @Override
    public Request lastRequest() {
        return state.lastRequest();
    }

    @Override
    public Browser clearCookies(){
        cookies.clearCookies();
        return this;
    }

    @Override
    public Browser clearCookie(String name){
        cookies.clearCookie(name);
        return this;
    }

    @Override
    public String getCookie(String name) {
        return cookies.getCookie(name);
    }

    @Override
    public Browser setCookie(String name, String value) {
        cookies.setCookie(name, value);
        return this;
    }

    @Override
    public Sequence<Pair<String, String>> cookies() {
        return cookies.cookies();
    }

    public static class functions {
        public static LogicalPredicate<Browser> isOnPage(final String relativeUrl) {
            return new LogicalPredicate<Browser>() {
                @Override
                public boolean matches(Browser other) {
                    return relativeUrl.equals(other.uri().toString());
                }
            };
        }

        public static Function1<Browser, String> url() {
            return browser -> browser.uri().toString();
        }
    }
}
