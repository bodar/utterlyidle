package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Sequence;

import java.io.StringReader;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.lines;

public class RequestParser {
    public RequestBuilder parse(String stringified) {
        HttpRequestParts parts = getParts(stringified);

        RequestBuilder requestBuilder = requestBuilder(parts.requestLine);
        addHeaders(requestBuilder,parts.headers);
        requestBuilder.withInput(parts.messageBody.getBytes());

        return requestBuilder;
    }

    private HttpRequestParts getParts(String stringified) {
        Sequence<String> headerAndBody = sequence(stringified.split("\r\n\r\n"));

        Sequence<String> headerLines = linesOf(headerAndBody.first());

        return new HttpRequestParts(headerLines.take(1).first(), headerLines.drop(1), (headerAndBody.size().equals(2) ? headerAndBody.second(): ""));
    }

    private RequestBuilder requestBuilder(String requestLine) {
        Sequence<String> wordsOfRequestLine = words(requestLine);
        return new RequestBuilder(wordsOfRequestLine.first(), wordsOfRequestLine.second());
    }

    private Sequence<String> linesOf(String string) {
        return lines(new StringReader(string));
    }

    private void addHeaders(RequestBuilder requestBuilder, Sequence<String> headerLines) {
        headerLines.fold(requestBuilder, addHeaders());
    }

    private Callable2<RequestBuilder, String, RequestBuilder> addHeaders() {
        return new Callable2<RequestBuilder, String, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, String headerLine) throws Exception {
                String[] nameAndValue = headerLine.split(": ");
                requestBuilder.withHeader(nameAndValue[0], nameAndValue.length == 2 ? nameAndValue[1] : "");
                return requestBuilder;
            }
        };
    }

    private Sequence<String> words(String stringSequence) {
        return sequence(stringSequence.split(" "));
    }

    private final class HttpRequestParts {
        public final String requestLine;
        public final Sequence<String> headers;
        public final String messageBody;

        private HttpRequestParts(String requestLine, Sequence<String> headers, String messageBody) {
            this.requestLine = requestLine;
            this.headers = headers;
            this.messageBody = messageBody;
        }
    }
}
