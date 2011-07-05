package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;

import java.io.StringReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.regex.MatchResult;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.Status.status;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.reverse;

public class HttpMessageParser {
    public static Request parseRequest(String requestMessage) {
        Sequence<Sequence<String>> httpRequestLines = httpMessageLines(requestMessage);

        return buildRequest(trim(httpRequestLines.first()).first(), trim(httpRequestLines.second()), httpRequestLines.last());
    }

    public static Response parseResponse(String responseMessage) {
        Sequence<Sequence<String>> httpResponseLines = httpMessageLines(responseMessage);

        return buildResponse(httpResponseLines.first().first(), httpResponseLines.second(), httpResponseLines.last());
    }

    private static Sequence<String> trim(Sequence<String> linesToTrim) {
        return linesToTrim.map(new Callable1<String, String>() {
            public String call(String s) throws Exception {
                return s.trim();
            }
        });
    }

    private static Request buildRequest(String requestLine, Sequence<String> headerLines, Sequence<String> messageBodyLines) {
        RequestBuilder requestBuilder = new RequestBuilder(toMethod(requestLine), toPath(requestLine));
        requestBuilder = headerLines.fold(requestBuilder, requestHeader());
        requestBuilder.withInput(toInput(messageBodyLines).getBytes());
        return requestBuilder.build();
    }

    private static Response buildResponse(String statusLine, Sequence<String> headerLines, Sequence<String> messageBodyLines) {
        Response response = Responses.response(toStatus(statusLine));
        headerLines.fold(response, responseHeader());
        return response.bytes(toInput(messageBodyLines).getBytes()).entity("");
    }

    private static Sequence<Sequence<String>> httpMessageLines(String requestMessage) {
        List<String> lines = lines(requestMessage);
        if (lines.size() == 0) throw new IllegalArgumentException("Http Message without a start line");
        return sequence(first(lines), headerLines(lines), entityLines(lines));
    }

    static Status toStatus(String statusLine) {
        try {
            MatchResult matchResult = regex("(\\d\\d\\d)(?: )?(.*)?").findMatches(statusLine).first();
            return status(parseInt(matchResult.group(1)), matchResult.group(2));
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Response without a status code", e);
        }
    }

    static Pair<String, String> toMethodAndPath(String requestLine) {
        return pair(toMethod(requestLine), toPath(requestLine));
    }

    static Pair<String, String> toFieldNameAndValue(String messageHeader) {
        Sequence<String> fieldNameAndValue = trim(regex(":").split(messageHeader));
        String value = emptyValue(fieldNameAndValue) ? "" : fieldNameAndValue.second();
        return pair(fieldNameAndValue.first(), value);
    }

    static String toPath(String requestLine) {
        try {
            return regex(" (.+) ").findMatches(requestLine).first().group(1).trim();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Request without a path", e);
        }
    }

    static String toMethod(String requestLine) {
        try {
            return regex("^(\\p{Upper})+ ").findMatches(requestLine).first().group().trim();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Request without a valid method", e);
        }
    }

    private static boolean emptyValue(Sequence<String> fieldNameAndValue) {
        return fieldNameAndValue.toList().size() == 1;
    }

    private static Sequence<String> headerLines(List<String> lines) {
        int index = separatorLineIndex(lines);
        return sequence(lines.subList(1, index != -1 ? index : 1));
    }

    private static String toInput(Sequence<String> entityLines) {
        return entityLines.fold(new StringBuilder(), addInputLine()).toString();
    }

    private static int separatorLineIndex(List<String> lines) {
        return lines.indexOf("");
    }

    private static Sequence<String> entityLines(List<String> lines) {
        int index = separatorLineIndex(lines);
        return index == -1 ? Sequences.<String>sequence() : sequence(lines.subList(index, lines.size()));
    }

    private static List<String> lines(String responseMessage) {
        return Strings.lines(new StringReader(responseMessage)).toList();
    }

    private static Sequence<String> first(List<String> lines) {
        return sequence(lines.get(0));
    }

    private static Callable2<StringBuilder, String, StringBuilder> addInputLine() {
        return new Callable2<StringBuilder, String, StringBuilder>() {
            public StringBuilder call(StringBuilder input, String entityLine) throws Exception {
                return input.append(entityLine);
            }
        };
    }

    private static Callable2<? super RequestBuilder, ? super String, RequestBuilder> requestHeader() {
        return new Callable2<RequestBuilder, String, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, String messageHeader) throws Exception {
                Pair<String, String> fieldNameAndValue = toFieldNameAndValue(messageHeader);
                return requestBuilder.withHeader(fieldNameAndValue.first(), fieldNameAndValue.second());
            }
        };
    }

    private static Callable2<? super Response, ? super String, Response> responseHeader() {
        return new Callable2<Response, String, Response>() {
            public Response call(Response response, String messageHeader) throws Exception {
                Pair<String, String> fieldNameAndValue = toFieldNameAndValue(messageHeader);
                return response.header(fieldNameAndValue.first(), fieldNameAndValue.second());
            }
        };
    }
}
