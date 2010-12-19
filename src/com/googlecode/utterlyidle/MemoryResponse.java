package com.googlecode.utterlyidle;

import java.io.ByteArrayOutputStream;

import static com.googlecode.totallylazy.Callables.returns;

public class MemoryResponse extends ContractEnforcingResponse {
    public MemoryResponse() {
        super(returns(new ByteArrayOutputStream()));
    }

    public static Response response() {
        return new MemoryResponse();
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + output.toString();
    }
}