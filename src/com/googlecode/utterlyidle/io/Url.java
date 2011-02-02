package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Runnable1;
import com.googlecode.totallylazy.regex.Regex;

import java.io.*;
import java.net.*;
import java.util.UUID;

import static com.googlecode.totallylazy.Pair.pair;

public class Url {
    private static Regex JarUrl = Regex.regex("jar:([^!]*)!(.*)");
    private final String value;

    private Url(String value) {
        this.value = value;
    }

    public Url replacePath(Path path) {
        if (JarUrl.matches(value)) {
            return new Url("jar:" + JarUrl.findMatches(value).head().group(1) + "!" + path.toString());
        }

        try {
            URI o = toURI();
            String spaceForQuery = o.getRawQuery() == null ? null : UUID.randomUUID().toString();

            URI n = new URI(o.getScheme(), o.getUserInfo(), o.getHost(), o.getPort(), path.toString(), spaceForQuery, o.getRawFragment());

            String newUri = o.getRawQuery() == null ? n.toString() : n.toString().replace(spaceForQuery, o.getRawQuery());
            return new Url(newUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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

    public Pair<Integer, String> get(String mimeType, Runnable1<InputStream> handler) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) openConnection();
            urlConnection.setRequestProperty("Accept", mimeType);
            InputStream inputStream = urlConnection.getInputStream();
            handler.run(inputStream);
            inputStream.close();
            return pair(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pair<Integer, String> put(String mimeType, Runnable1<OutputStream> handler) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", mimeType);
            OutputStream outputStream = urlConnection.getOutputStream();
            handler.run(outputStream);
            outputStream.close();
            return pair(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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