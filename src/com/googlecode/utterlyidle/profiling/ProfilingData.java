package com.googlecode.utterlyidle.profiling;

import com.googlecode.lazyrecords.Logger;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.UriTemplate.trimSlashes;

public class ProfilingData implements Logger {
    public final List<Map<String, ?>> requests = new ArrayList<Map<String, ?>>();
    public final List<Map<String, ?>> queries = new ArrayList<Map<String, ?>>();
    private final Redirector redirector;

    public ProfilingData(Redirector redirector) {
        this.redirector = redirector;
    }

    public ProfilingData add(Request request, Response response, Number milliseconds) {
        Uri uri = modify(request).replaceQuery("profile", "").uri();
        Uri link = redirector.absoluteUriOf(uri.path(trimSlashes(uri.path())));
        requests.add(map(pair("uri", request.uri()), pair("link", link), pair("status", response.status()), pair("milliseconds", milliseconds)));
        return this;
    }

    @Override
    public ProfilingData log(Map<String, ?> data) {
        queries.add(data);
        return this;
    }
}
