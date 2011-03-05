package com.googlecode.utterlyidle.simpleframework;

import static com.googlecode.totallylazy.Bytes.bytes;

import com.googlecode.totallylazy.*;

import static com.googlecode.totallylazy.Exceptions.printStackTrace;
import static com.googlecode.totallylazy.Maps.entryToPair;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Runnables.write;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Using.using;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;

import com.googlecode.utterlyidle.*;

import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.io.Url.url;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.HttpHeaders;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RestContainer implements Container {
    private final Application applcation;

    public RestContainer(Application applcation) {
        this.applcation = applcation;
    }

    public void handle(Request request, Response response) {
        try {
            com.googlecode.utterlyidle.Response applicationResponse = getResponse(request);
            mapTo(applicationResponse, response);
            response.commit();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private com.googlecode.utterlyidle.Response getResponse(Request request) throws Exception {
        try {
            return applcation.handle(request(request));
        } catch (Throwable e) {
            StringWriter stringWriter = new StringWriter();
            using(new PrintWriter(stringWriter), printStackTrace(e));
            return response(INTERNAL_SERVER_ERROR, headerParameters(pair(CONTENT_TYPE, TEXT_PLAIN)), stringWriter.toString());
        }
    }

    private void mapTo(com.googlecode.utterlyidle.Response applicationResponse, Response response) throws IOException {
        response.setCode(applicationResponse.status().code());
        sequence(applicationResponse.headers()).fold(response, mapHeaders());
        using(response.getOutputStream(), write(applicationResponse.bytes()));
    }

    private Callable2<Response, Pair<String, String>, Response> mapHeaders() {
        return new Callable2<Response, Pair<String, String>, Response>() {
            public Response call(Response response, Pair<String, String> applicationHeader) throws Exception {
                response.set(applicationHeader.first(), applicationHeader.second());
                return response;
            }
        };
    }

    private com.googlecode.utterlyidle.Request request(Request request) throws IOException {
        return Requests.request(
                request.getMethod(),
                request.getPath().toString(),
                query(request),
                headers(request),
                bytes(request.getInputStream()));
    }

    private HeaderParameters headers(final Request request) {
        return headerParameters(sequence(request.getNames()).map(headerValue(request)));
    }

    private Callable1<String, Pair<String, String>> headerValue(final Request request) {
        return new Callable1<String, Pair<String, String>>() {
            public Pair<String, String> call(String headerName) throws Exception {
                return pair(headerName, request.getValue(headerName));
            }
        };
    }

    private QueryParameters query(Request request) {
        return QueryParameters.parse(url(request.getTarget()).getQuery());
    }
}
