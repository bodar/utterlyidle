package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;

import java.io.StringReader;
import java.util.List;
import java.util.regex.MatchResult;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.Status.status;
import static java.lang.Integer.parseInt;

public class HttpMessageParser {
    public static Request parseRequest(String requestMessage) {
        List<String> lines = lines(requestMessage);

        String requestLine = first(lines);
        Sequence<String> headerLines = headerLines(lines);
        Sequence<String> entityLines = entityLines(lines);

        RequestBuilder requestBuilder = new RequestBuilder(toMethod(requestLine), toPath(requestLine));
        requestBuilder = headerLines.fold(requestBuilder, requestHeader());
        String input = toInput(entityLines);
        requestBuilder.withInput(input.getBytes());

        return requestBuilder.build();
    }

    public static Response parseResponse(String responseMessage) {
        List<String> lines = lines(responseMessage);

        String statusLine = first(lines);
        Sequence<String> headerLines = headerLines(lines);
        Sequence<String> entityLines = entityLines(lines);

        Response response = Responses.response(toStatus(statusLine));
        return headerLines.fold(response, responseHeader()).bytes(toInput(entityLines).getBytes());
    }

    static Status toStatus(String statusLine) {
        MatchResult matchResult = regex("(\\d\\d\\d) (.*)").findMatches(statusLine).first();
        return status(parseInt(matchResult.group(1)), matchResult.group(2));
    }

    static Pair<String, String> toMethodAndPath(String requestLine) {
        return pair(toMethod(requestLine), toPath(requestLine));
    }

    static Pair<String, String> toFieldNameAndValue(String messageHeader) {
        Sequence<String> fieldNameAndValue = regex(": ").split(messageHeader);
        String value = emptyValue(fieldNameAndValue) ? "" : fieldNameAndValue.second();
        return pair(fieldNameAndValue.first(), value);
    }

    static String toPath(String requestLine) {
        return regex(" (.+) ").findMatches(requestLine).first().group(1);
    }

    static String toMethod(String requestLine) {
        return regex("^(\\p{Upper})+").findMatches(requestLine).first().group();
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

    private static String first(List<String> lines) {
        return lines.get(0);
    }

    private static String last(List<String> lines) {
        return lines.get(lines.size() - 1);
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
