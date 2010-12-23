package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import javax.ws.rs.core.HttpHeaders;
import java.io.*;

public class SiteMeshResponse extends DelegatingResponse {
    private final Request request;
    private final Decorators decorators;
    private final OutputStream outputStream;

    public SiteMeshResponse(Request request, final Response response, Decorators decorators) {
        this(request, response, decorators,  new ByteArrayOutputStream());
    }

    public SiteMeshResponse(Request request, final Response response, Decorators decorators, OutputStream outputStream) {
        super(response);
        this.request = request;
        this.decorators = decorators;
        this.outputStream = outputStream;
    }

    @Override
    public Response status(Status value) {
        return ensureSiteMeshIsStillWrappingResponse(super.status(value));
    }

    @Override
    public Response header(String name, String value) {
        return ensureSiteMeshIsStillWrappingResponse(super.header(name, value));
    }

    @Override
    public Response entity(Object value) {
        return ensureSiteMeshIsStillWrappingResponse(super.entity(value));
    }

    private SiteMeshResponse ensureSiteMeshIsStillWrappingResponse(final Response response) {
        return new SiteMeshResponse(request, response, decorators, outputStream);
    }

    @Override
    public OutputStream output() {
        if(shouldDecorate()){
            return outputStream;
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
        return outputStream.toString();
    }
}
