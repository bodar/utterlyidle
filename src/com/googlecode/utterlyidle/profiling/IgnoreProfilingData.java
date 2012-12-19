package com.googlecode.utterlyidle.profiling;

import com.googlecode.lazyrecords.Logger;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum IgnoreProfilingData implements ProfilingData {
    Instance {
        @Override
        public ProfilingData add(Request request, Response response, Number milliseconds) {
            return this;
        }

        @Override
        public Logger log(Map<String, ?> stringMap) {
            return this;
        }

        @Override
        public List<Map<String, ?>> requests() {
            return Collections.emptyList();
        }

        @Override
        public List<Map<String, ?>> queries() {
            return Collections.emptyList();
        }
    }
}
