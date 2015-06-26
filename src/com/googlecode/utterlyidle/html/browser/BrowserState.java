package com.googlecode.utterlyidle.html.browser;

import com.googlecode.totallylazy.functions.Lazy;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.html.Html;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static java.lang.String.format;

public interface BrowserState {
    Html html() throws Exception;
    Option<Html> htmlOption();
    Uri uri();
    Status status();
    Response lastResponse();
    Request lastRequest();

    class constructors {
        public static BrowserState empty() {
            return new EmptyBrowserState();
        }
        public static BrowserState browserState(final Request request, final Response response) {
            return new BrowserState() {
                final Request lastRequest = request;
                final Response lastResponse = response;

                final Lazy<Option<Html>> lastHtml = Lazy.lazy(() -> {
                    try {
                        return some(Html.html(response));
                    } catch (Exception e) {
                        System.out.println(format("Error while parsing html.\nRequest was:\n%s\n\nResponse was:\n%s", lastRequest, lastResponse));
                        e.printStackTrace();
                        return none(Html.class);
                    }
                });

                @Override
                public Html html() throws Exception {
                    return htmlOption()
                            .getOrThrow(new IllegalStateException(
                                    format("No HTML found.\nRequest was:\n%s\n\nResponse was:\n%s", lastRequest, lastResponse)));
                }

                @Override
                public Option<Html> htmlOption() {
                    return call(lastHtml);
                }

                @Override
                public Uri uri() {
                    return lastRequest.uri();
                }

                @Override
                public Status status() {
                    return lastResponse.status();
                }

                @Override
                public Response lastResponse() {
                    return lastResponse;
                }

                @Override
                public Request lastRequest() {
                    return lastRequest;
                }
            };
        }

    }

}
