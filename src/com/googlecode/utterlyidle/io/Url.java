package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.regex.Regex;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Runnables.doNothing;

public class Url {
    private static Regex JarUrl = Regex.regex("jar:([^!]*)!(.*)");
    private final String value;

    protected Url(String value) {
        this.value = value;
    }

    public Url replacePath(Path path) {
        if (JarUrl.matches(value)) {
            return new Url("jar:" + JarUrl.findMatches(value).head().group(1) + "!" + path.toString());
        }

        URI o = toURI();
        return new Url(toString(o.getScheme(), o.getRawUserInfo(), o.getHost(), o.getPort(), path.toString(), o.getRawQuery(), o.getRawFragment()));
    }

    protected String toString(final String scheme, final String usernamePassword, final String host, final int port, final String path, final String query, final String fragment) {
        return new StringBuilder().append(formatScheme(scheme)).append(formatUsernamePassword(usernamePassword)).append(formatHost(host)).append(formatPort(port)).
                append(path).append(formatQuery(query)).append(formatFragment(fragment)).toString();
    }

    private String formatScheme(String scheme) {
        return scheme == null ? "" : scheme + "://";
    }

    private String formatHost(String host) {
        return host == null ? "" : host;
    }

    private String formatFragment(String fragment) {
        return fragment == null ? "" : "#" + fragment;
    }

    private String formatQuery(String query) {
        return query == null ? "" : "?" + query;
    }

    private String formatPort(int port) {
        return port == -1 ? "" : ":" + String.valueOf(port) ;
    }

    private String formatUsernamePassword(String usernamePassword) {
        return usernamePassword == null ? "" : usernamePassword + "@";
    }

    public URI toURI() {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public HierarchicalPath path() {
        if (JarUrl.matches(value)) {
            return new HierarchicalPath(JarUrl.findMatches(value).head().group(2));
        }

        return new HierarchicalPath(toURI().getRawPath());
    }

    public Url parent() {
        return replacePath(path().parent());
    }

    public Reader reader() throws IOException {
        return new InputStreamReader(inputStream());
    }

    public InputStream inputStream() throws IOException {
        return openConnection().getInputStream();
    }

    public URLConnection openConnection() {
        try {
            final URLConnection urlConnection = new URL(value).openConnection();
            urlConnection.setUseCaches(true);
            return urlConnection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pair<Integer, String> get(String mimeType, Callable1<InputStream, Void> handler) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) openConnection();
            urlConnection.setRequestProperty("Accept", mimeType);
            return doRequest(urlConnection, handler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pair<Integer, String> put(String mimeType, Callable1<OutputStream, Void> handler) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", mimeType);
            urlConnection.setDoInput(true);

            using(urlConnection.getOutputStream(), handler);

            return pair(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pair<Integer, String> post(String mimeType, Callable1<OutputStream, Void> requestContent) {
        return post(mimeType, requestContent, doNothing(InputStream.class));
    }

    public Pair<Integer, String> post(String mimeType, Callable1<OutputStream, Void> requestContent, Callable1<InputStream, Void> responseHandler) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", mimeType);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            using(urlConnection.getOutputStream(), requestContent);

            return doRequest(urlConnection, responseHandler);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Pair<Integer, String> doRequest(HttpURLConnection urlConnection, Callable1<InputStream, Void> responseHandler) throws IOException {
        using(inputStream(urlConnection), responseHandler);
        return pair(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
    }

    public static InputStream inputStream(HttpURLConnection urlConnection) throws IOException {
        if (urlConnection.getResponseCode() >= 400) {
            return urlConnection.getErrorStream();
        } else {
            return urlConnection.getInputStream();
        }
    }

    public Pair<Integer, String> delete() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) openConnection();
            urlConnection.setRequestMethod("DELETE");
            return pair(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Url) {
            return toString().equals(((Url) other).toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    public URL toURL() {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Url url(String value) {
        Url url = new Url(value);
        return url.replacePath(url.path());
    }

    public static Url url(URL value) {
        return url(value.toString());
    }

    public static Url url(URI value) {
        return url(value.toString());
    }

    public String getQuery() {
        return toURI().getRawQuery();
    }

    public boolean isAbsolute() {
        return toURI().isAbsolute();
    }

}