package com.googlecode.utterlyidle.profiling;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.Auditor;

import java.util.Date;

public class ProfilingAuditor implements Auditor {

    @Override
    public void audit(Pair<Request, Date> request, Pair<Response, Date> response) throws Exception {
        long difference = response.second().getTime() - request.second().getTime();
        if (StatsCollector.getInstance() != null) {
            StatsCollector.getInstance().collectExecutionTime(request.first(), response.first(), difference);
        }
    }
}
