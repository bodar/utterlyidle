package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Responses;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Exceptions.printStackTrace;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.ClientAddress.clientAddress;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HeaderParameters.withXForwardedFor;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;

public class RestContainer implements Container {
    private final Application application;

    public RestContainer(Application application) {
        this.application = application;
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
            return application.handle(request(request));
        } catch (Throwable e) {
            StringWriter stringWriter = new StringWriter();
            using(new PrintWriter(stringWriter), printStackTrace(e));
            return ResponseBuilder.response(INTERNAL_SERVER_ERROR).
                    header(CONTENT_TYPE, TEXT_PLAIN).
                    entity(stringWriter.toString()).
                    build();
        }
    }

    private void mapTo(com.googlecode.utterlyidle.Response applicationResponse, Response response) throws IOException {
        response.setCode(applicationResponse.status().code());
        sequence(applicationResponse.headers()).fold(response, mapHeaders());
        for (Integer integer : Responses.contentLength(applicationResponse)) {
            response.setContentLength(integer);
        }
        using(response.getOutputStream(), applicationResponse.entity().transferFrom());
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
                withXForwardedFor(clientAddress(request.getClientAddress().getAddress()), headers(request)),
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
        return QueryParameters.parse(uri(request.getTarget()).query());
    }
}
