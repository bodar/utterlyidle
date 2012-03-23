package com.googlecode.utterlyidle.profiling;

import com.googlecode.lazyrecords.Logger;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Pair.pair;

public class ProfilingData implements Logger {
    public final List<Map<String, ?>> requests = new ArrayList<Map<String, ?>>();
    public final List<Map<String, ?>> queries = new ArrayList<Map<String, ?>>();

    public ProfilingData add(Uri uri, Status status, Number milliseconds) {
        requests.add(map(pair("path", uri), pair("status", status), pair("milliseconds", milliseconds)));
        return this;
    }

    @Override
    public Logger log(Map<String, ?> data) {
        queries.add(data);
        return this;
    }
}
