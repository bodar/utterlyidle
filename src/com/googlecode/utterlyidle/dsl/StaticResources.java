package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.annotations.Hidden;
import com.googlecode.utterlyidle.RegisteredResources;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.io.Url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.utterlyidle.io.Converter.asString;

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
