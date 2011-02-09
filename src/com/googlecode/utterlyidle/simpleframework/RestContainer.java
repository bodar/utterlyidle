package com.googlecode.utterlyidle.simpleframework;

import static com.googlecode.totallylazy.Bytes.bytes;
import com.googlecode.totallylazy.Callable1;
import static com.googlecode.totallylazy.Maps.entryToPair;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Callable2;
import static com.googlecode.totallylazy.Sequences.repeat;
import static com.googlecode.totallylazy.Sequences.sequence;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HeaderParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import com.googlecode.utterlyidle.QueryParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import com.googlecode.utterlyidle.Requests;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import java.io.IOException;

public class RestContainer implements Container {
    private final Application applcation;

    public RestContainer(Application applcation) {
        this.applcation = applcation;
    }

    public void handle(Request request, Response response) {
        try {
            com.googlecode.utterlyidle.Response applicationResponse = applcation.handle(request(request));
            mapTo(applicationResponse,  response);

            response.getPrintStream().close();
            response.commit();
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void mapTo(com.googlecode.utterlyidle.Response applicationResponse, Response response) throws IOException {
        response.setCode(applicationResponse.status().code());
        sequence(applicationResponse.headers()).fold(response, mapHeaders());
        response.getOutputStream().write(applicationResponse.bytes());
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
        return headerParameters(sequence(request.getNames()).flatMap(headerValues(request)));
    }

    private Callable1<String, Iterable<? extends Pair<String, String>>> headerValues(final Request request) {
        return new Callable1<String, Iterable<? extends Pair<String, String>>>() {
            public Iterable<? extends Pair<String, String>> call(String headerName) throws Exception {
                return repeat(headerName).zip(sequence(request.getValues(headerName)));
            }
        };
    }

    private QueryParameters query(Request request) {
        return queryParameters(sequence(request.getQuery().entrySet()).map(entryToPair(String.class, String.class)));
    }
}
