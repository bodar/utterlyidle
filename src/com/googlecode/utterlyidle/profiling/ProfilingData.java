package com.googlecode.utterlyidle.profiling;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Pair.pair;

public class ProfilingData {
    private final List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();

    public ProfilingData add(Uri uri, Status status, Number milliseconds) {
        data.add(map(pair("path", uri), pair("statusCode", status), pair("executionTime", milliseconds)));
        return this;
    }

    public List<Map<String, ?>> data() {
        return data;
    }
}
