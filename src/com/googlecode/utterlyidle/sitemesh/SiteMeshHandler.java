package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.googlecode.totallylazy.Sequences.sequence;

public class SiteMeshHandler implements RequestHandler {
    private final RequestHandler requestHandler;
    private final Decorators decorators;

    public SiteMeshHandler(final RequestHandler requestHandler, final Decorators decorators) {
        this.requestHandler = requestHandler;
        this.decorators = decorators;
    }

    public void handle(final Request request, final Response response) throws Exception {
        requestHandler.handle(request, response.output(new SiteMeshOutputStream(request, response, response.output())));
    }

    class SiteMeshOutputStream extends FilterOutputStream {
        private final Request request;
        private final Response response;
        private final OutputStream destination;

        public SiteMeshOutputStream(Request request, Response response, OutputStream destination) {
            super(new ByteArrayOutputStream()); // this is 'out'
            this.request = request;
            this.response = response;
            this.destination = destination;
        }

        @Override
        public void close() throws IOException {
            final Decorator decorator = decorators.getDecoratorFor(request, response);
            final OutputStreamWriter writer = new OutputStreamWriter(destination);
            writer.write(decorator.decorate(getOriginalContent()));
            writer.close();
            super.close();
        }

        private String getOriginalContent() {
            return out.toString();
        }
    }
}