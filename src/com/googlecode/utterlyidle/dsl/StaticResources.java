package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Responses;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;

import java.net.URL;

import static com.googlecode.totallylazy.io.Uri.uri;

public class StaticResources {
    public Response get(URL baseUrl, String filename) {
        try {
            Uri original = uri(baseUrl).mergePath(filename);
            Uri normalise = original.removeDotSegments();
            if (!original.equals(normalise)) return Responses.response(Status.NOT_FOUND);
            HttpHandler handler = new ClientHttpHandler();
            return handler.handle(RequestBuilder.get(original).build());
        } catch (Exception e) {
            throw LazyException.lazyException(e);
        }
    }
}