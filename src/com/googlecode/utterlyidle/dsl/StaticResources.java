package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.Callable1;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Closeables.using;

public class StaticResources {
    public byte[] get(URL baseUrl, String filename){
        try {
            URL fileUrl = new URL(baseUrl.toString() + filename);
            return using(fileUrl.openStream(), new Callable1<InputStream, byte[]>() {
                public byte[] call(InputStream stream) throws Exception {
                    return bytes(stream);
                }
            });
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
