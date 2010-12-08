package com.googlecode.utterlyidle.sitemesh;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class SiteMeshOutputStream extends FilterOutputStream {
    private final OutputStream destination;
    private final StringTemplateDecorator decorator;

    public SiteMeshOutputStream(OutputStream destination, final StringTemplateDecorator decorator) {
        super(new ByteArrayOutputStream()); // this is 'out'
        this.destination = destination;
        this.decorator = decorator;
    }

    @Override
    public void close() throws IOException {
        decorator.decorate(new PropertyMapParser().parse(out.toString())).writeTo(destination);
        super.close();
    }
}
