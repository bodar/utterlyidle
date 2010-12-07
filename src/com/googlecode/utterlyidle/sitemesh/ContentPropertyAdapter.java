package com.googlecode.utterlyidle.sitemesh;

import org.sitemesh.content.ContentProperty;

public class ContentPropertyAdapter implements Property{
    private final ContentProperty contentProperty;

    public ContentPropertyAdapter(ContentProperty contentProperty) {
        this.contentProperty = contentProperty;
    }

    public boolean hasChild(String name) {
        return contentProperty.hasChild(name);
    }

    public Property getChild(String name) {
        return new ContentPropertyAdapter(contentProperty);
    }

    public String getValue() {
        return contentProperty.getValue();
    }
}
