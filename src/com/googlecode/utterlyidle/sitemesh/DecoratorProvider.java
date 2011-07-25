package com.googlecode.utterlyidle.sitemesh;

public interface DecoratorProvider {
    Decorator get(TemplateName templateName);
}
