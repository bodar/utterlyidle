package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Responses.seeOther;
import static com.googlecode.utterlyidle.handlers.ConvertExtensionToAcceptHeader.Replacements.replacements;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ConvertExtensionToAcceptHeaderTest {
    private StubHttpHandler httpHandler = new StubHttpHandler();

    @Test
    public void findsExtensionInRequestUrl() {
        assertThat(extensionOf("/spong"), is(nullValue()));
        assertThat(extensionOf("/spong?param=not.an.extension"), is(nullValue()));
        assertThat(extensionOf("/spong/groupof.stuff/actual.html?moose=still+not+monkey"), is(".html"));
        assertThat(extensionOf("/spong/groupof.stuff/actual.html"), is(".html"));
        assertThat(extensionOf("actual.html"), is(".html"));
        assertThat(extensionOf("actual.html?param=something"), is(".html"));
        assertThat(extensionOf("something/.html"), is(".html"));
        assertThat(extensionOf("something/.html?spong=moomintroll"), is(".html"));
        assertThat(extensionOf("/something.html?getUrl=/something_else.xml"), is(".html"));
    }

    @Test
    public void removesExtensionFromRequestUrl() throws Exception {
        ConvertExtensionToAcceptHeader converter = new ConvertExtensionToAcceptHeader(
                replacements(pair("properties", "text/plain"), pair("html", "text/html")),
                httpHandler);

        assertUrlConversion(converter, "/resource.properties?twigs=berries", "/resource?twigs=berries");
        assertUrlConversion(converter, "/resource.html?twigs=berries", "/resource?twigs=berries");
        assertUrlConversion(converter, "/.html?twigs=berries", "/?twigs=berries");

        assertUrlConversion(converter,
                "/something.html?getUrl=/something_else.xml",
                "/something?getUrl=/something_else.xml");

        assertUrlConversion(converter, "/resource.notmapped?twigs=berries", "/resource.notmapped?twigs=berries");
    }

    @Test
    public void replacesAcceptHeaderWithImpliedMediaType() throws Exception {
        ConvertExtensionToAcceptHeader converter = new ConvertExtensionToAcceptHeader(
                replacements(pair("properties", "text/plain"), pair("html", "text/html")),
                httpHandler);

        assertHeaderConversion(converter, "resource.properties?twigs=berries", "text/plain");
        assertHeaderConversion(converter, "resource.html?twigs=berries", "text/html");
    }

    @Test
    public void addsExtensionBackInToLocationHeaderForRedirects() throws Exception {
        ConvertExtensionToAcceptHeader converter = new ConvertExtensionToAcceptHeader(
                replacements(pair("properties", "text/plain"), pair("html", "text/html")),
                httpHandler);

        httpHandler.respondsWith(seeOther("/redirect/to/here"));
        assertThat(
                converter.handle(get("anything.properties").build()).header(LOCATION),
                is("/redirect/to/here.properties"));

        httpHandler.respondsWith(seeOther("/redirect/to/here.moo?queryparams=some"));
        assertThat(
                converter.handle(get("anything.properties").build()).header(LOCATION),
                is("/redirect/to/here.moo.properties?queryparams=some"));
    }

    private void assertHeaderConversion(ConvertExtensionToAcceptHeader converter, String url, String expectedMimeType) throws Exception {
        Request request = get(url).build();
        converter.handle(request);

        assertThat(request.headers().getValue(HttpHeaders.ACCEPT), is(expectedMimeType));
    }

    private void assertUrlConversion(ConvertExtensionToAcceptHeader converter, String original, String expectedAfterConversion) throws Exception {
        Request request = get(original).build();
        converter.handle(request);

        assertThat(request.uri(), is(uri(expectedAfterConversion)));
    }

    private String extensionOf(String url) {
        return ConvertExtensionToAcceptHeader.fileExtension(uri(url)).getOrNull();
    }

    private static class StubHttpHandler implements HttpHandler {
        public Response response = response();

        public Response handle(Request request) throws Exception {
            return response;
        }

        public void respondsWith(Response response) {
            this.response = response;
        }
    }
}
