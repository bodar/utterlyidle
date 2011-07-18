package com.googlecode.utterlyidle.sitemesh;

import java.io.IOException;

public interface Decorator {
    String decorate(String content) throws IOException;
}
