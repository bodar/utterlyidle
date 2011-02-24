package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.Callable1;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class HttpURLConnections {
    public static Callable1<? super HttpURLConnection, OutputStream> getOutputStream() {
        return new Callable1<HttpURLConnection, OutputStream>() {
            public OutputStream call(HttpURLConnection httpURLConnection) throws Exception {
                return httpURLConnection.getOutputStream();
            }
        };
    }

    public static Callable1<? super HttpURLConnection, InputStream> getInputStream() {
        return new Callable1<HttpURLConnection, InputStream>() {
            public InputStream call(HttpURLConnection httpURLConnection) throws Exception {
                return httpURLConnection.getInputStream();
            }
        };
    }

    public static Callable1<? super HttpURLConnection, InputStream> getErrorStream() {
        return new Callable1<HttpURLConnection, InputStream>() {
            public InputStream call(HttpURLConnection httpURLConnection) throws Exception {
                return httpURLConnection.getErrorStream();
            }
        };
    }
}
