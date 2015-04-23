package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Bytes;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Exceptions;
import com.googlecode.totallylazy.Files;
import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.annotations.multimethod;
import com.googlecode.totallylazy.collections.CloseableList;
import com.googlecode.totallylazy.multi;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.proxies.NoProxy;
import com.googlecode.utterlyidle.proxies.ProxyFor;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Fields.access;
import static com.googlecode.totallylazy.Fields.fields;
import static com.googlecode.totallylazy.Fields.name;
import static com.googlecode.totallylazy.LazyException.lazyException;
import static com.googlecode.totallylazy.Maps.pairs;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.totallylazy.collections.CloseableList.constructors.closeableList;
import static com.googlecode.totallylazy.numbers.Numbers.greaterThan;
import static com.googlecode.totallylazy.numbers.Numbers.zero;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.HttpHeaders.LAST_MODIFIED;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.status;

public class ClientHttpHandler implements HttpClient, Closeable {
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;
    private final ProxyFor proxies;
    private final CloseableList<InputStream> closeables = closeableList();
    private final Integer streamingSize = Integer.getInteger("utterlyidle.client.stream.size", 4000);
    private final Boolean disableStreaming = Boolean.getBoolean("utterlyidle.client.stream.disable");

    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    public ClientHttpHandler() {
        this(0);
    }

    public ClientHttpHandler(int timeoutMillis) {
        this(timeoutMillis, timeoutMillis);
    }

    public ClientHttpHandler(int timeoutMillis, ProxyFor proxyFor) {
        this(timeoutMillis, timeoutMillis, proxyFor);
    }

    public ClientHttpHandler(int connectTimeoutMillis, int readTimeoutMillis) {
        this(connectTimeoutMillis, readTimeoutMillis, NoProxy.instance);
    }

    public ClientHttpHandler(int connectTimeoutMillis, int readTimeoutMillis, ProxyFor proxies) {
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.proxies = proxies;
    }

    public ClientHttpHandler(RequestTimeout requestTimeout) {
        this(requestTimeout.value());
    }

    public Response handle(final Request request) throws Exception {
        URLConnection connection = openConnection(request.uri());
        connection.setUseCaches(false);
        connection.setConnectTimeout(connectTimeoutMillis);
        connection.setReadTimeout(readTimeoutMillis);
        if (connection instanceof HttpURLConnection) {
            return handle(request, (HttpURLConnection) connection);
        }
        return handle(request, connection);
    }

    private URLConnection openConnection(final Uri uri) {
        final URL url = uri.toURL();
        return proxies.proxyFor(uri).map(new Mapper<Proxy, URLConnection>() {
            @Override
            public URLConnection call(final Proxy proxy) throws Exception {
                return url.openConnection(proxy);
            }
        }).getOrElse(new Function<URLConnection>() {
            @Override
            public URLConnection call() throws Exception {
                return url.openConnection();
            }
        });
    }

    private multi multi;
    private Response handle(final Request request, final URLConnection connection) throws IOException {
        if(multi == null) multi = new multi(){};
        return multi.<Response>methodOption(request, connection).getOrElse(new Function<Response>() {
            @Override
            public Response call() throws Exception {
                return defaultHandle(request, connection);
            }
        });
    }

    private Response defaultHandle(final Request request, final URLConnection connection) throws IOException {
        try {
            sendRequest(request, connection);
            return createResponse(connection, OK, entity(connection));
        } catch (FileNotFoundException e) {
            return errorResponse(NOT_FOUND, e);
        }
    }

    @multimethod
    private Response handle(Request request, FileURLConnection connection) throws IOException {
        if (request.method().equals(HttpMethod.PUT)) {
            File file = uri(connection.getURL()).toFile();
            Files.write(request.entity().asBytes(), file);
            for (String date : request.headers().valueOption(LAST_MODIFIED)) file.setLastModified(Dates.parse(date).getTime());
            return ResponseBuilder.response(Status.CREATED).header(HttpHeaders.LOCATION, connection.getURL()).build();
        }
        return defaultHandle(request, connection);
    }

    @multimethod
    private Response handle(Request request, HttpURLConnection connection) throws IOException {
        try {
            connection.setInstanceFollowRedirects(false);
            setHttpMethod(request, connection);
            Status status = sendHttpRequest(request, connection);
            return createResponse(connection, status, entity(connection));
        } catch (SocketException ex) {
            return errorResponse(Status.CONNECTION_REFUSED, ex);
        } catch (SocketTimeoutException ex) {
            return errorResponse(Status.CLIENT_TIMEOUT, ex);
        }
    }

    private static final Field httpMethod = access(fields(HttpURLConnection.class).find(where(name, is("method"))).get());
    private void setHttpMethod(final Request request, final HttpURLConnection connection) {
        try {
            httpMethod.set(connection, request.method());
        } catch (IllegalAccessException e) {
            throw lazyException(e);
        }
    }

    private void sendRequest(Request request, URLConnection connection) throws IOException {
        sequence(request.headers()).fold(connection, requestHeaders());
        if (request.entity().length().is(zero)) return;

        connection.setDoOutput(true);
        using(connection.getOutputStream(), request.entity().writer());
    }

    private Status sendHttpRequest(final Request request, final HttpURLConnection connection) throws IOException {
        sendRequest(request, connection);
        return status(connection);
    }

    private Object entity(final URLConnection connection) throws IOException {
        Option<Integer> length = contentLength(connection);
        return handleStreamingContent(length, connection.getInputStream());
    }

    private Object entity(final HttpURLConnection connection) throws IOException {
        Option<Integer> length = contentLength(connection);
        if (connection.getResponseCode() >= 400) {
            return handleStreamingContent(length, connection.getErrorStream());
        }
        return handleStreamingContent(length, connection.getInputStream());
    }

    private Object handleStreamingContent(final Option<Integer> length, final InputStream inputStream) {
        if( !disableStreaming && (length.isEmpty() || length.is(greaterThan(streamingSize)))) return closeables.manage(inputStream);
        return using(inputStream, bytes());
    }

    private static Option<Integer> contentLength(final URLConnection urlConnection) {
        return option(urlConnection.getHeaderField(CONTENT_LENGTH)).flatMap(new Mapper<String, Integer>() {
            @Override
            public Integer call(final String s) throws Exception {
                return Integer.valueOf(s.trim());
            }
        }.optional());
    }

    public static Response errorResponse(Status status, Exception e) {
        return response(status, sequence(pair(CONTENT_TYPE, MediaType.TEXT_PLAIN)), Exceptions.asString(e));
    }

    private Response createResponse(URLConnection connection, Status status, Object entity) {
        final ResponseBuilder builder = pairs(connection.getHeaderFields()).
                filter(where(first(String.class), is(not(equalIgnoringCase(HttpHeaders.TRANSFER_ENCODING))))).
                fold(ResponseBuilder.response(status).entity(entity),
                        responseHeaders());
        builder.replaceHeaders(LAST_MODIFIED, new Date(connection.getLastModified()));
        return builder.build();
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

    @Override
    public void close() throws IOException {
        closeables.close();
    }
}
