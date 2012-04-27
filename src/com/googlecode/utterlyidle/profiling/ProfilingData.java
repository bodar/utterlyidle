package com.googlecode.utterlyidle.profiling;

import com.googlecode.lazyrecords.Logger;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.List;
import java.util.Map;

public interface ProfilingData extends Logger {
    ProfilingData add(Request request, Response response, Number milliseconds);

    List<Map<String, ?>> requests();

    List<Map<String, ?>> queries();
}
