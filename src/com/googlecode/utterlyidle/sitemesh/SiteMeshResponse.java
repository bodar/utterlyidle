package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import javax.ws.rs.core.HttpHeaders;
import java.io.*;

public class SiteMeshResponse extends DelegatingResponse {
    private final Request request;
    private final Decorators decorators;
    private final OutputStream buffer = new ByteArrayOutputStream();

    public SiteMeshResponse(Request request, final Response response, Decorators decorators) {
        super(response);
        this.request = request;
        this.decorators = decorators;
    }

    @Override
    public OutputStream output() {
        if(shouldDecorate()){
            return buffer;
        }
        return response.output();
    }

    @Override
    public void close() throws IOException {
        if(shouldDecorate()){
            Decorator decorator = decorators.getDecoratorFor(request, this);
            String result = decorator.decorate(originalContent());
            Writer writer = new OutputStreamWriter(response.output());
            writer.write(result);
            writer.close();
        } else {
            super.close();
        }
    }

    private boolean shouldDecorate() {
        return header(HttpHeaders.CONTENT_TYPE).contains("text/html");
    }

    public String originalContent() {
        return buffer.toString();
    }
}
