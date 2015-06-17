package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.Function1;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class HttpURLConnections {
    public static Function1<? super HttpURLConnection, OutputStream> getOutputStream() {
        return new Function1<HttpURLConnection, OutputStream>() {
            public OutputStream call(HttpURLConnection httpURLConnection) throws Exception {
                return httpURLConnection.getOutputStream();
            }
        };
    }

    public static Function1<? super HttpURLConnection, InputStream> getInputStream() {
        return new Function1<HttpURLConnection, InputStream>() {
            public InputStream call(HttpURLConnection httpURLConnection) throws Exception {
                return httpURLConnection.getInputStream();
            }
        };
    }

    public static Function1<? super HttpURLConnection, InputStream> getErrorStream() {
        return new Function1<HttpURLConnection, InputStream>() {
            public InputStream call(HttpURLConnection httpURLConnection) throws Exception {
                return httpURLConnection.getErrorStream();
            }
        };
    }
}
