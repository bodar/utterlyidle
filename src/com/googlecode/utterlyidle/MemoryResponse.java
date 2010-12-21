package com.googlecode.utterlyidle;

import java.io.ByteArrayOutputStream;

import static com.googlecode.totallylazy.Callables.returns;
import static com.googlecode.totallylazy.Callers.call;

public class MemoryResponse extends ContractEnforcingResponse {
    public MemoryResponse() {
        super(returns(new ByteArrayOutputStream()));
    }

    public static Response response() {
        return new MemoryResponse();
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + call(output).toString();
    }
}