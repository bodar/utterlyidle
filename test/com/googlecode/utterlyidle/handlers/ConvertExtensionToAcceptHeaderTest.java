package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Responses.seeOther;
import static com.googlecode.utterlyidle.handlers.ConvertExtensionToAcceptHeader.Replacements.replacements;
import static com.googlecode.utterlyidle.handlers.RecordingHttpHandler.recordingHttpHandler;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ConvertExtensionToAcceptHeaderTest {
    @Test
    public void findsExtensionInRequestUrl() {
        assertThat(extensionOf(""), is(nullValue()));
        assertThat(extensionOf("/"), is(nullValue()));
        assertThat(extensionOf("/.csv"), is(".csv"));
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
        assertUrlConversion("/resource.properties?twigs=berries", "/resource?twigs=berries");
        assertUrlConversion("/resource.html?twigs=berries", "/resource?twigs=berries");
        assertUrlConversion("/.html?twigs=berries", "/?twigs=berries");

        assertUrlConversion(
                "/something.html?getUrl=/something_else.xml",
                "/something?getUrl=/something_else.xml");

        assertUrlConversion("/resource.notmapped?twigs=berries", "/resource.notmapped?twigs=berries");
    }

    @Test
    public void replacesAcceptHeaderWithImpliedMediaType() throws Exception {
        assertHeaderConversion("resource.properties?twigs=berries", "text/plain");
        assertHeaderConversion("resource.html?twigs=berries", "text/html");
    }

    @Test
    public void addsExtensionBackInToLocationHeaderForRedirects() throws Exception {
        ConvertExtensionToAcceptHeader converter = converter(respondsWith(seeOther("/redirect/to/here")));
        assertThat(
                header(converter.handle(get("anything.properties").build()), LOCATION),
                is("/redirect/to/here.properties"));

        converter = converter(respondsWith(seeOther("/redirect/to/here.moo?queryparams=some")));
        assertThat(
                header(converter.handle(get("anything.properties").build()), LOCATION),
                is("/redirect/to/here.moo.properties?queryparams=some"));
    }

    private void assertHeaderConversion(String url, String expectedMimeType) throws Exception {
        RecordingHttpHandler httpHandler = respondsWith(response().build());

        ConvertExtensionToAcceptHeader converter = converter(httpHandler);

        Request request = get(url).build();
        converter.handle(request);

        assertThat(httpHandler.requests().head().headers().getValue(HttpHeaders.ACCEPT), is(expectedMimeType));
    }

    private ConvertExtensionToAcceptHeader converter(RecordingHttpHandler httpHandler) {
        return new ConvertExtensionToAcceptHeader(
                replacements(pair("properties", "text/plain"), pair("html", "text/html")),
                httpHandler);
    }

    private RecordingHttpHandler respondsWith(Response response) {
        return recordingHttpHandler(returnsResponse(response));
    }

    private void assertUrlConversion(String original, String expectedAfterConversion) throws Exception {
        RecordingHttpHandler httpHandler = respondsWith(response().build());

        ConvertExtensionToAcceptHeader converter = converter(httpHandler);

        Request request = get(original).build();
        converter.handle(request);

        assertThat(httpHandler.requests().head().uri(), is(uri(expectedAfterConversion)));
    }



    private String extensionOf(String url) {
        return ConvertExtensionToAcceptHeader.fileExtension(uri(url)).getOrNull();
    }
}