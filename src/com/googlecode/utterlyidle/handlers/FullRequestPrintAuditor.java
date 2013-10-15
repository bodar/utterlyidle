package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Date;

import static com.googlecode.totallylazy.time.Dates.RFC3339withMilliseconds;
import static java.lang.String.format;

public class FullRequestPrintAuditor implements Auditor {
    @Override
    public void audit(final Pair<Request, Date> request, final Pair<Response, Date> response) throws Exception {
        System.out.println(format("Sent at " + RFC3339withMilliseconds().format(request.second())));
        System.out.println(request.first());
        System.out.println();
        System.out.println(format("Received at " + RFC3339withMilliseconds().format(response.second())));
        System.out.println(response.first());
        System.out.println();
    }
}
