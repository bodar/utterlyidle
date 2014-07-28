package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;

import java.net.URL;

public class StaticResources {
    public Response get(URL baseUrl, String filename) {
        try {
            Uri fileUrl = Uri.uri(baseUrl.toString() + filename);
            HttpHandler handler = new ClientHttpHandler();
            return handler.handle(RequestBuilder.get(fileUrl).build());
        } catch (Exception e) {
            throw LazyException.lazyException(e);
        }
    }
}
