package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.records.Transaction;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class TransactionHttpHandler implements HttpHandler{
   private final HttpHandler requestHandler;
    private final Transaction transaction;

    public TransactionHttpHandler(HttpHandler requestHandler, Transaction transaction) {
        this.requestHandler = requestHandler;
        this.transaction = transaction;
    }

    public Response handle(Request request) throws Exception {
        try {
            Response response = requestHandler.handle(request);
            transaction.commit();
            return response;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
