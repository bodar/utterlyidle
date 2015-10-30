package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.functions.Function2;
import com.googlecode.utterlyidle.*;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Exceptions.printStackTrace;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.ClientAddress.clientAddress;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.Protocol.HTTP;
import static com.googlecode.utterlyidle.Protocol.HTTPS;
import static com.googlecode.utterlyidle.RequestEnricher.requestEnricher;
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
                    contentType(TEXT_PLAIN).
                    entity(stringWriter.toString()).
                    build();
        }
    }

    private void mapTo(com.googlecode.utterlyidle.Response applicationResponse, Response response) throws IOException {
        response.setCode(applicationResponse.status().code());
        sequence(applicationResponse.headers()).fold(response, mapHeaders());
        for (String integer : applicationResponse.headers().valueOption(HttpHeaders.CONTENT_LENGTH)) {
            response.setContentLength(Integer.parseInt(integer));
        }
        using(response.getOutputStream(), applicationResponse.entity().writer());
    }

    private Function2<Response, Pair<String, String>, Response> mapHeaders() {
        return (response, applicationHeader) -> {
            response.setValue(applicationHeader.first(), applicationHeader.second());
            return response;
        };
    }

    private com.googlecode.utterlyidle.Request request(Request frameworkRequest) throws IOException {
        com.googlecode.utterlyidle.Request request = Requests.request(
                frameworkRequest.getMethod(),
                frameworkRequest.getPath().toString(),
                query(frameworkRequest),
                headers(frameworkRequest),
                frameworkRequest.getInputStream());
        return requestEnricher(
                clientAddress(frameworkRequest.getClientAddress().getAddress()),
                frameworkRequest.isSecure() ? HTTPS : HTTP)
                .enrich(request);
    }

    private HeaderParameters headers(final Request request) {
        return headerParameters(sequence(request.getNames()).map(headerValue(request)));
    }

    private Function1<String, Pair<String, String>> headerValue(final Request request) {
        return headerName -> pair(headerName, request.getValue(headerName));
    }

    private QueryParameters query(Request request) {
        return QueryParameters.parse(uri(request.getTarget()).query());
    }
}
