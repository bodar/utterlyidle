package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Bytes;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Files;
import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.annotations.multimethod;
import com.googlecode.totallylazy.multi;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Maps.pairs;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.HttpHeaders.LAST_MODIFIED;
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
        connection.setUseCaches(false);
        connection.setReadTimeout(milliseconds);
        return handle(request, connection);
    }

    private Response handle(final Request request, final URLConnection connection) throws IOException {
        return new multi(){}.<Response>methodOption(request, connection).getOrElse(new Function<Response>() {
            @Override
            public Response call() throws Exception {
                return defaultHandle(request, connection);
            }
        });
    }

    private Response defaultHandle(final Request request, final URLConnection connection) throws IOException {
        try {
            sendRequest(request, connection);
            return createResponse(connection, OK, using(connection.getInputStream(), bytes()));
        } catch (FileNotFoundException e) {
            return createResponse(connection, NOT_FOUND, new byte[0]);
        }
    }

    @multimethod
    private Response handle(Request request, FileURLConnection connection) throws IOException {
        if (request.method().equals(HttpMethod.PUT)) {
            File file = uri(connection.getURL()).toFile();
            Files.write(request.entity().asBytes(), file);
            file.setLastModified(Dates.parse(request.headers().getValue(LAST_MODIFIED)).getTime());
            return ResponseBuilder.response(Status.CREATED).header(HttpHeaders.LOCATION, connection.getURL()).build();
        }
        return defaultHandle(request, connection);
    }

    @multimethod
    private Response handle(Request request, HttpURLConnection connection) throws IOException {
        try {
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(request.method());
            sendRequest(request, connection);
            Status status = sendRequest(connection);
            byte[] bytes = using(inputStream(connection), bytes());
            return createResponse(connection, status, bytes);
        } catch (SocketException ex) {
            return response(Status.CONNECTION_REFUSED);
        } catch (SocketTimeoutException ex) {
            return response(Status.CLIENT_TIMEOUT);
        }
    }

    private Status sendRequest(final HttpURLConnection connection) throws IOException {
        return status(connection);
    }

    public static InputStream inputStream(HttpURLConnection urlConnection) throws IOException {
        if (urlConnection.getResponseCode() >= 400) {
            return urlConnection.getErrorStream();
        } else {
            return urlConnection.getInputStream();
        }
    }

    private Response createResponse(URLConnection connection, Status status, byte[] bytes) {
        final ResponseBuilder builder = pairs(connection.getHeaderFields()).
                filter(where(first(String.class), is(not(equalIgnoringCase(HttpHeaders.TRANSFER_ENCODING))))).
                fold(ResponseBuilder.response(status).entity(bytes),
                        responseHeaders());
        builder.replaceHeaders(LAST_MODIFIED, new Date(connection.getLastModified()));
        if (!builder.build().headers().contains(CONTENT_LENGTH)) {
            return builder.header(CONTENT_LENGTH, bytes.length).build();
        }
        return builder.build();
    }

    private void sendRequest(Request request, URLConnection connection) throws IOException {
        sequence(request.headers()).fold(connection, requestHeaders());
        if (Integer.valueOf(request.headers().getValue(CONTENT_LENGTH)) > 0) {
            connection.setDoOutput(true);
            using(connection.getOutputStream(), request.entity().transferFrom());
        }
    }

    private static Callable2<? super URLConnection, ? super Pair<String, String>, URLConnection> requestHeaders() {
        return new Callable2<URLConnection, Pair<String, String>, URLConnection>() {
            public URLConnection call(URLConnection connection, Pair<String, String> header) throws Exception {
                connection.setRequestProperty(header.first(), header.second());
                return connection;
            }
        };
    }

    private static Callable2<ResponseBuilder, Pair<String, List<String>>, ResponseBuilder> responseHeaders() {
        return new Callable2<ResponseBuilder, Pair<String, List<String>>, ResponseBuilder>() {
            public ResponseBuilder call(ResponseBuilder response, final Pair<String, List<String>> entry) throws Exception {
                return sequence(entry.second()).fold(response, responseHeader(entry.first()));
            }
        };
    }

    private static Callable2<ResponseBuilder, String, ResponseBuilder> responseHeader(final String key) {
        return new Callable2<ResponseBuilder, String, ResponseBuilder>() {
            public ResponseBuilder call(ResponseBuilder response, String value) throws Exception {
                if (key != null) {
                    return response.header(key, value);
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
