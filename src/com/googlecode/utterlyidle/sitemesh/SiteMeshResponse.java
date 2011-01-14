package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.*;

public class SiteMeshResponse extends DelegatingResponse implements Flushable{
    private final Request request;
    private final Decorators decorators;
    private final OutputStream capturedContent;

    public SiteMeshResponse(Request request, final Response response, Decorators decorators) {
        super(response);
        this.request = request;
        this.response = response;
        this.decorators = decorators;
        this.capturedContent = new ByteArrayOutputStream();
    }

    public Response status(Status value) {
        response = response.status(value);
        return this;
    }

    public Response header(String name, String value) {
        response = response.header(name, value);
        return this;
    }

    public Response entity(Object value) {
        response = response.entity(value);
        return this;
    }

    public OutputStream output() {
        if(shouldDecorate()){
            return capturedContent;
        }
        return super.output();
    }

    public void flush() throws IOException {
        if(shouldDecorate()){
            Decorator decorator = decorators.getDecoratorFor(request, this);
            String result = decorator.decorate(capturedContent());
            Writer writer = new OutputStreamWriter(response.output());
            writer.write(result);
            writer.flush();
        }
    }

    public boolean shouldDecorate() {
        String header = header(HttpHeaders.CONTENT_TYPE);
        return header != null && header.contains(MediaType.TEXT_HTML);
    }

    public String capturedContent() {
        return capturedContent.toString();
    }
}
