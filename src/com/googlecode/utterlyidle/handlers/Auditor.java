package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Date;

public interface Auditor {
    void audit(Pair<Request, Date> request, Pair<Response, Date> response) throws Exception;
}
