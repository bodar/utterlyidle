package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Closeables;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LastExceptionsHandler implements HttpHandler {
    private final HttpHandler handler;
    private final LastExceptions lastExceptions;

    public LastExceptionsHandler(final HttpHandler handler, final LastExceptions lastExceptions) {
        this.handler = handler;
        this.lastExceptions = lastExceptions;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        try {
            final Response response = handler.handle(request);
            response.entity().writer(catchStreamingExceptions(request, response.entity().writer()));
            return response;
        } catch (Exception e) {
            lastExceptions.put(request, e);
            throw e;
        }
    }

    private Block<OutputStream> catchStreamingExceptions(final Request request, final Block<OutputStream> oldWriter) {
        return new Block<OutputStream>() {
            @Override
            protected void execute(final OutputStream outputStream) throws Exception {
                try {
                    oldWriter.call(new IgnoreCloseOutputStream(outputStream));
                } catch (Exception e) {
                    lastExceptions.put(request, e);
                } finally {
                    Closeables.close(outputStream);
                }
            }
        };
    }

    private class IgnoreCloseOutputStream extends OutputStream {
        private final OutputStream outputStream;

        public IgnoreCloseOutputStream(final OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(final int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public void write(final byte[] b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            outputStream.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            outputStream.flush();
        }

        @Override
        public void close() throws IOException {
        }
    }
}
