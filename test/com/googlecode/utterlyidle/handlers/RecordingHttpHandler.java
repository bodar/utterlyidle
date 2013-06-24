package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.collections.PersistentList.constructors.cons;
import static com.googlecode.totallylazy.collections.PersistentList.constructors.empty;
import static com.googlecode.totallylazy.collections.PersistentList.constructors.reverse;

public class RecordingHttpHandler implements HttpHandler {
    private PersistentList<Request> requests = empty();
    private final HttpHandler delegate;

    private RecordingHttpHandler(HttpHandler delegate) {
        this.delegate = delegate;
    }

    public static RecordingHttpHandler recordingHttpHandler(HttpHandler delegate) {
        return new RecordingHttpHandler(delegate);
    }

    public Response handle(Request request) throws Exception {
        requests = cons(request, requests);
        return delegate.handle(request);
    }

    public PersistentList<Request> requests() {
        return reverse(requests);
    }

    public Request lastRequest() {
        return requests.head();
    }
}