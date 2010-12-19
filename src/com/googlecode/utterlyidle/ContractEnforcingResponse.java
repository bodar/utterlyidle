package com.googlecode.utterlyidle;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Callables.returns;
import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.callables.LazyCallable.lazy;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;

public class ContractEnforcingResponse implements Response {
    protected Status code;
    protected Callable<? extends OutputStream> output;
    private final HeaderParameters headers = headerParameters();

    public ContractEnforcingResponse(Callable<? extends OutputStream> output) {
        this.output = lazy(output);
    }

    public static Response response(OutputStream output) {
        return new ContractEnforcingResponse(returns(output));
    }

    public Status status() {
        return code;
    }

    public Response status(Status value) {
        this.code = value;
        return this;
    }

    public String header(String name) {
        return headers.getValue(name);
    }

    public Iterable<String> headers(String name) {
        return headers.getValues(name);
    }

    public Response header(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public OutputStream output() {
        if(code == null){
            throw new IllegalStateException("You must set the HTTP status before you can write content to the output stream");
        }
        if(!headers.contains(HttpHeaders.CONTENT_TYPE)){
            throw new IllegalStateException("You must set the HTTP Content-Type header before you can write content to the output stream");
        }
        return call(output);
    }

    public Response output(OutputStream outputStream) {
        output = lazy(returns(outputStream));
        return this;
    }

    public void close() throws IOException {
        call(output).close();
    }

    @Override
    public String toString() {
        return String.format("HTTP/1.1 %s\n%s\n", code, headers); 
    }
}