package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.Url;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.googlecode.utterlyidle.BasePath.basePath;

public class Requests {
    public static MemoryRequest request(String method, Url requestUri, HeaderParameters headers, byte[] input) {
        return request(method, requestUri, headers, input, basePath("/"));
    }

    public static MemoryRequest request(String method, Url requestUri, HeaderParameters headers, byte[] input, BasePath basePath) {
        return new MemoryRequest(method, requestUri, headers, input, basePath);
    }

    public static Request request(String method, String path, QueryParameters query, HeaderParameters headers, byte[] input) {
        return request(method, Url.url(path + query.toString()), headers, input);
    }

    public static byte[] getBytes(InputStream stream) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int read = stream.read(buffer);
            while (read > 0) {
                outputStream.write(buffer, 0, read);
                read = stream.read(buffer);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
