package com.googlecode.utterlyidle.profiling;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.Request.Builder.modify;
import static com.googlecode.utterlyidle.Request.Builder.query;
import static com.googlecode.utterlyidle.UriTemplate.trimSlashes;

public class InMemoryProfilingData implements ProfilingData {
    private final List<Map<String, ?>> requests = new CopyOnWriteArrayList<Map<String, ?>>();
    private final List<Map<String, ?>> queries = new CopyOnWriteArrayList<Map<String, ?>>();
    private final Redirector redirector;

    public InMemoryProfilingData(Redirector redirector) {
        this.redirector = redirector;
    }

    @Override
    public ProfilingData add(Request request, Response response, Number milliseconds) {
        Uri uri = modify(request, query("profile", "")).uri();
        Uri link = redirector.absoluteUriOf(uri.path(trimSlashes(uri.path())));
        requests.add(map(pair("uri", request.uri()), pair("link", link), pair("status", response.status()), pair("milliseconds", milliseconds)));
        return this;
    }

    @Override
    public ProfilingData log(Map<String, ?> data) {
        queries.add(data);
        return this;
    }

    public List<Map<String, ?>> requests() {
        return requests;
    }

    @Override
    public List<Map<String, ?>> queries() {
        return queries;
    }
}
