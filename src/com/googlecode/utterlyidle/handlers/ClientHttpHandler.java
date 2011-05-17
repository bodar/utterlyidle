package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.status;
import static com.googlecode.utterlyidle.io.Url.inputStream;

public class ClientHttpHandler implements HttpHandler {
    public Response handle(final Request request) throws Exception {
        URL url = new URL(request.url().toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(request.method());
        sequence(request.headers()).fold(connection, requestHeaders());
        if (Integer.valueOf(request.headers().getValue(HttpHeaders.CONTENT_LENGTH)) > 0) {
            connection.setDoOutput(true);
            using(connection.getOutputStream(), copyRequestEntity(request));
        }

        Status status = status(connection.getResponseCode(), connection.getResponseMessage());
        return sequence(connection.getHeaderFields().entrySet()).
                fold(response(status).
                        bytes(using(inputStream(connection), bytes())),
                        responseHeaders());
    }

    private Callable1<OutputStream, Void> copyRequestEntity(final Request request) {
        return new Callable1<OutputStream, Void>() {
            public Void call(OutputStream outputStream) throws Exception {
                outputStream.write(request.input());
                return Runnables.VOID;
            }
        };
    }

    private static Callable2<? super HttpURLConnection, ? super Pair<String, String>, HttpURLConnection> requestHeaders() {
        return new Callable2<HttpURLConnection, Pair<String, String>, HttpURLConnection>() {
            public HttpURLConnection call(HttpURLConnection connection, Pair<String, String> header) throws Exception {
                connection.setRequestProperty(header.first(), header.second());
                return connection;
            }
        };
    }

    private static Callable2<Response, Map.Entry<String, List<String>>, Response> responseHeaders() {
        return new Callable2<Response, Map.Entry<String, List<String>>, Response>() {
            public Response call(Response response, final Map.Entry<String, List<String>> entry) throws Exception {
                return sequence(entry.getValue()).fold(response, responseHeader(entry));
            }
        };
    }

    private static Callable2<Response, String, Response> responseHeader(final Map.Entry<String, List<String>> entry) {
        return new Callable2<Response, String, Response>() {
            public Response call(Response response, String value) throws Exception {
                if (entry.getKey() != null) {
                    return response.header(entry.getKey(), value);
                }
                return response;
            }
        };
    }

    public static Callable1<InputStream, byte[]> bytes() {
        return new Callable1<InputStream, byte[]>() {
            public byte[] call(InputStream stream) throws Exception {
                return Bytes.bytes(stream);
            }
        };
    }
}
