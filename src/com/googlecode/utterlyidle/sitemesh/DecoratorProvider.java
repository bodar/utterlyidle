package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;

public interface DecoratorProvider {
    Decorator get(TemplateName templateName, Request request);
}
