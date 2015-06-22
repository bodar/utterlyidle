package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.Function1;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class HttpURLConnections {
    public static Function1<? super HttpURLConnection, OutputStream> getOutputStream() {
        return HttpURLConnection::getOutputStream;
    }

    public static Function1<? super HttpURLConnection, InputStream> getInputStream() {
        return HttpURLConnection::getInputStream;
    }

    public static Function1<? super HttpURLConnection, InputStream> getErrorStream() {
        return HttpURLConnection::getErrorStream;
    }
}
