package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Application;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.concurrent.Callable;

class SiteMeshOutputStream extends FilterOutputStream {
    private final OutputStream original;

    public SiteMeshOutputStream(OutputStream output) {
        super(new ByteArrayOutputStream());
        original = output;
    }

    @Override
    public void flush() throws IOException {
        CharBuffer buffer = CharBuffer.wrap(out.toString());
        PropertyMap properties = new PropertyMapParser().parse(buffer);
        super.flush();
    }

}
