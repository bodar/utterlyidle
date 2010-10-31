package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Pair.pair;

public class UrlEncodedMessage {
    private static final String charSet = "UTF-8";

    public static List<Pair<String, String>> parse(String value) {
        List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        if(value == null){
            return result;
        }

        for (String pair : value.split("&")) {
            String[] nameValue = pair.split("=");
            if(nameValue.length == 2){
                result.add(pair(decode(nameValue[0]), decode(nameValue[1])));
            }
        }
        return result;
    }

    public static String toString(Iterable<Pair<String, String>> pairs) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Pair<String,String> pair :pairs){
            if (first) {
                first = false;
            } else {
                builder.append("&");
            }
            builder.append(encode(pair.first())).append("=").append(encode(pair.second()));
        }
        return builder.toString();
    }


    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, charSet);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, charSet);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
