package com.googlecode.utterlyidle.profiling;

import com.googlecode.totallylazy.Triple;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.ArrayList;
import java.util.List;

public class StatsCollector {
    private List<Triple<Request, Response, Long>> executionTimes = new ArrayList<Triple<Request, Response, Long>>();
    private static final ThreadLocal<StatsCollector> threadLocal = new ThreadLocal<StatsCollector>();

    private StatsCollector() {}

    public static StatsCollector getInstance() {
        return threadLocal.get();
    }

    public List<Triple<Request, Response, Long>> executionTimes() {
        return executionTimes;
    }

    public void collectExecutionTime(Request request, Response response, long executionTime) {
        executionTimes.add(Triple.triple(request, response, executionTime));
    }

    public static StatsCollector begin() {
        StatsCollector statsCollector = new StatsCollector();
        threadLocal.set(statsCollector);
        return statsCollector;
    }

    public static void end() {
        threadLocal.remove();
    }
}
