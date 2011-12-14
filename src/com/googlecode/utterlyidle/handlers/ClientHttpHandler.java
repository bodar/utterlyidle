package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Bytes;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Runnables;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.status;

public class ClientHttpHandler implements HttpClient {
    private final int milliseconds;

    public ClientHttpHandler() {
        this(0);
    }

    public ClientHttpHandler(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Response handle(final Request request) throws Exception {
        URL url = new URL(request.uri().toString());
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(milliseconds);
        connection.setReadTimeout(milliseconds);
        if (connection instanceof HttpURLConnection) {
            return handle(request, (HttpURLConnection) connection);
        }
        return handle(request, connection);
    }

    private Response handle(Request request, URLConnection connection) throws IOException {
        sendRequest(request, connection);
        try {
            return createResponse(connection, OK, using(connection.getInputStream(), bytes()));
        } catch (FileNotFoundException e) {
            return createResponse(connection, NOT_FOUND, new byte[0]);
        }
    }

    private Response handle(Request request, HttpURLConnection connection) throws IOException {
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(request.method());
        sendRequest(request, connection);
        Status status = status(connection.getResponseCode(), connection.getResponseMessage());
        byte[] bytes = using(inputStream(connection), bytes());
        return createResponse(connection, status, bytes);
    }

    public static InputStream inputStream(HttpURLConnection urlConnection) throws IOException {
        if (urlConnection.getResponseCode() >= 400) {
            return urlConnection.getErrorStream();
        } else {
            return urlConnection.getInputStream();
        }
    }

    private Response createResponse(URLConnection connection, Status status, byte[] bytes) {
        return sequence(connection.getHeaderFields().entrySet()).
                fold(response(status).
                        bytes(bytes),
                        responseHeaders());
    }

    private void sendRequest(Request request, URLConnection connection) throws IOException {
        sequence(request.headers()).fold(connection, requestHeaders());
        if (Integer.valueOf(request.headers().getValue(HttpHeaders.CONTENT_LENGTH)) > 0) {
            connection.setDoOutput(true);
            using(connection.getOutputStream(), copyRequestEntity(request));
        }
    }

    private Callable1<OutputStream, Void> copyRequestEntity(final Request request) {
        return new Callable1<OutputStream, Void>() {
            public Void call(OutputStream outputStream) throws Exception {
                outputStream.write(request.entity());
                return Runnables.VOID;
            }
        };
    }

    private static Callable2<? super URLConnection, ? super Pair<String, String>, URLConnection> requestHeaders() {
        return new Callable2<URLConnection, Pair<String, String>, URLConnection>() {
            public URLConnection call(URLConnection connection, Pair<String, String> header) throws Exception {
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
