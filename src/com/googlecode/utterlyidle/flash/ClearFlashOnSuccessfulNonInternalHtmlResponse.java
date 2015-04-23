package com.googlecode.utterlyidle.flash;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.List;

import static com.googlecode.totallylazy.Lists.list;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_XHTML_XML;
import static com.googlecode.utterlyidle.MediaType.TEXT_HTML;
import static com.googlecode.utterlyidle.Status.NOT_MODIFIED;

public class ClearFlashOnSuccessfulNonInternalHtmlResponse implements ClearFlashPredicate {
    private final InternalRequestMarker internalRequestMarker;
    private final List<String> acceptableMediaTypes = list(TEXT_HTML, APPLICATION_XHTML_XML);

    public ClearFlashOnSuccessfulNonInternalHtmlResponse(InternalRequestMarker internalRequestMarker) {
        this.internalRequestMarker = internalRequestMarker;
    }

    @Override
    public boolean matches(Pair<Request, Response> requestResponse) {
        Request request = requestResponse.first();
        Response response = requestResponse.second();

        return (response.status().isSuccessful() || response.status().equals(NOT_MODIFIED))
                && isAcceptableMediaType(response)
                && !internalRequestMarker.isInternal(request);
    }

    private boolean isAcceptableMediaType(Response response) {
        Option<String> contentType = response.headers().valueOption(HttpHeaders.CONTENT_TYPE);
        return !contentType.isEmpty() && acceptableMediaTypes.contains(mediaType(contentType.get()));
    }

    private String mediaType(String contentTypeHeader) {
        return contentTypeHeader.split(";")[0];
    }
}
