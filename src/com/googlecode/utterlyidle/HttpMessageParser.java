package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Function2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Strings;

import java.io.StringReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.Request.Builder.entity;
import static com.googlecode.utterlyidle.Request.Builder.header;
import static com.googlecode.utterlyidle.Request.Builder.request;
import static com.googlecode.utterlyidle.Status.status;
import static java.lang.Integer.parseInt;

public class HttpMessageParser {
    public static Request parseRequest(String requestMessage) {
        Sequence<Sequence<String>> httpRequestLines = httpMessageLines(requestMessage);

        return buildRequest(trim(httpRequestLines.first()).first(), trim(httpRequestLines.second()), httpRequestLines.last());
    }

    static final Pattern pattern = Pattern.compile("(.+?)(?:\r\n\r\n|\n\n)(.*)", Pattern.DOTALL);
    public static Response parseResponse(String responseMessage) {
        final Matcher matcher = pattern.matcher(responseMessage);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("A response must have a blank line between the headers and the body.");
        }
        final MatchResult matchResult = matcher.toMatchResult();
        final Sequence<String> statusAndHeaders = Strings.lines(new StringReader(matchResult.group(1)));
        final String body = matchResult.group(2);


        return buildResponse(statusAndHeaders.first().trim(), trim(statusAndHeaders.tail()), body);
    }

    private static Request buildRequest(String requestLine, Sequence<String> headerLines, Sequence<String> messageBodyLines) {
        return request(toMethod(requestLine), toPath(requestLine),
                header(headerLines.map(HttpMessageParser::toFieldNameAndValue)),
                entity(toInput(messageBodyLines)));
    }

    private static Response buildResponse(String statusLine, Sequence<String> headerLines, String body) {
        ResponseBuilder responseBuilder = ResponseBuilder.response(toStatus(statusLine));
        headerLines.fold(responseBuilder, responseHeader());
        return responseBuilder.entity(body).build();
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
        Sequence<String> fieldNameAndValue = trim(sequence(messageHeader.split(":", 2)));
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
            return regex("^(\\p{Alpha})+ ").findMatches(requestLine).first().group().trim().toUpperCase();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Request without a valid method", e);
        }
    }

    private static boolean emptyValue(Sequence<String> fieldNameAndValue) {
        return fieldNameAndValue.toList().size() == 1;
    }

    private static Sequence<String> headerLines(List<String> lines) {
        int index = separatorLineIndex(lines);
        return sequence(lines.subList(1, index != -1 ? index : lines.size()));
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

    private static Function2<StringBuilder, String, StringBuilder> addInputLine() {
        return StringBuilder::append;
    }

    private static Function2<ResponseBuilder, String, ResponseBuilder> responseHeader() {
        return (responseBuilder, messageHeader) -> {
            Pair<String, String> fieldNameAndValue = toFieldNameAndValue(messageHeader);
            return responseBuilder.header(fieldNameAndValue.first(), fieldNameAndValue.second());
        };
    }

    private static Sequence<String> trim(Sequence<String> linesToTrim) {
        return linesToTrim.map(String::trim);
    }
}
