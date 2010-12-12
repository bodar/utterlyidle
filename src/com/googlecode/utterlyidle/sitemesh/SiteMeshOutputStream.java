package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SiteMeshOutputStream extends FilterOutputStream {
    private final Request request;
    private final Response response;
    private final OutputStream destination;
    private Decorators decorators;

    public SiteMeshOutputStream(Request request, Response response, OutputStream destination, Decorators decorators) {
        super(new ByteArrayOutputStream()); // this is 'out'
        this.request = request;
        this.response = response;
        this.destination = destination;
        this.decorators = decorators;
    }

    @Override
    public void close() throws IOException {
        final Decorator decorator = decorators.getDecoratorFor(request, response);
        final OutputStreamWriter writer = new OutputStreamWriter(destination);
        writer.write(decorator.decorate(getOriginalContent()));
        writer.close();
        super.close();
    }

    public String getOriginalContent() {
        return out.toString();
    }
}

