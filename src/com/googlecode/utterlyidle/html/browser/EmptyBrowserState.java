package com.googlecode.utterlyidle.html.browser;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.html.Html;

import static com.googlecode.totallylazy.Option.none;

public class EmptyBrowserState implements BrowserState {
    @Override
    public Html html() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public Option<Html> htmlOption() {
        return none();
    }

    @Override
    public Uri uri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Status status() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response lastResponse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Request lastRequest() {
        throw new UnsupportedOperationException();
    }
}
