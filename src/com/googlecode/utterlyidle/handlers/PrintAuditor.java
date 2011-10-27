package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.io.PrintStream;
import java.util.Date;

public class PrintAuditor implements Auditor{
    private final PrintStream printStream;

    public PrintAuditor(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void audit(Pair<Request, Date> requestPair, Pair<Response, Date> responsePair) throws Exception {
        Request request = requestPair.first();
        Date start = requestPair.second();
        Response response = responsePair.first();
        Date end = responsePair.second();
        printStream.println(String.format("%s %s -> %s in %s msecs", request.method(), request.uri(), response.status(), end.getTime() - start.getTime()));

    }
}
