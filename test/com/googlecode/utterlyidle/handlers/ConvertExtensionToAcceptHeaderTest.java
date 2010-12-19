package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.regex.Matches;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.handlers.ConvertExtensionToAcceptHeader.Replacements.replacements;
import static com.googlecode.utterlyidle.io.Url.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConvertExtensionToAcceptHeaderTest {
    private RequestHandler stubRequestHandler = new RequestHandler() {
        public void handle(Request request, Response response) throws Exception {
        }
    };

    @Test
    public void shouldFindExtensionsInRequestUrl() {
        assertThat(extensionOf("/spong/groupof.stuff/actual.html?moose=still+not+monkey"), is(".html"));
        assertThat(extensionOf("/spong/groupof.stuff/actual.html"), is(".html"));
        assertThat(extensionOf("actual.html"), is(".html"));
        assertThat(extensionOf("something/.html"), is(".html"));
        assertThat(extensionOf("something/.html?spong=moomintroll"), is(".html"));
    }

    @Test
    public void shouldRemoveExtensionFromRequestUrl() throws Exception {
        ConvertExtensionToAcceptHeader converter = new ConvertExtensionToAcceptHeader(
                replacements(pair("properties", "text/plain"), pair("html", "text/html")),
                stubRequestHandler);

        assertUrlConversion(converter, "resource.properties?twigs=berries", "resource?twigs=berries");
        assertUrlConversion(converter, "resource.html?twigs=berries",       "resource?twigs=berries");
        assertUrlConversion(converter, ".html?twigs=berries",       "?twigs=berries");

        assertUrlConversion(converter, "resource.notmapped?twigs=berries",  "resource.notmapped?twigs=berries");
    }

    @Test
    public void shouldReplaceAcceptHeader() throws Exception {
        ConvertExtensionToAcceptHeader converter = new ConvertExtensionToAcceptHeader(
                replacements(pair("properties", "text/plain"), pair("html", "text/html")),
                stubRequestHandler);

        assertHeaderConversion(converter, "resource.properties?twigs=berries",  "text/plain");
        assertHeaderConversion(converter, "resource.html?twigs=berries",        "text/html");
    }

    private void assertHeaderConversion(ConvertExtensionToAcceptHeader converter, String url, String expectedMimeType) throws Exception {
        Request request = RequestBuilder.get(url).build();
        converter.handle(request, null);

        assertThat(request.headers().getValue(HttpHeaders.ACCEPT), is(expectedMimeType));
    }

    private void assertUrlConversion(ConvertExtensionToAcceptHeader converter, String original, String expectedAfterConversion) throws Exception {
        Request request = RequestBuilder.get(original).build();
        converter.handle(request, null);

        assertThat(request.url(), is(url(expectedAfterConversion)));
    }

    private String extensionOf(String url) {
        Matches matches = ConvertExtensionToAcceptHeader.FILE_EXTENSION.findMatches(url);
        return matches.isEmpty() ? null : matches.head().group(1);
    }
}
