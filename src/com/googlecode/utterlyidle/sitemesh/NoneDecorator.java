package com.googlecode.utterlyidle.sitemesh;

import java.io.IOException;

public class NoneDecorator implements Decorator {
    public String decorate(String content) throws IOException {
        return content;
    }
}
