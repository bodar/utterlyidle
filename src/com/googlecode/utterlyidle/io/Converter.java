package com.googlecode.utterlyidle.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Converter {
    public static String asString(InputStream stream) {
        return asString(new InputStreamReader(stream));
    }

    public static String asString(Reader reader) {
        try {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[512];
            int read = reader.read(buffer);
            while (read > 0) {
                builder.append(buffer, 0, read);
                read = reader.read(buffer);
            }
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
