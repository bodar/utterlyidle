package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Callable1;
import org.sitemesh.content.ContentProperty;

import static com.googlecode.totallylazy.Sequences.sequence;

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

    public String getName() {
        return contentProperty.getName();
    }

    public String getValue() {
        return contentProperty.getValue();
    }

    public Iterable<Property> getChildren() {
        return sequence(contentProperty.getChildren()).map(asProperty());
    }

    private Callable1<? super ContentProperty, Property> asProperty() {
        return new Callable1<ContentProperty, Property>() {
            public Property call(ContentProperty contentProperty) throws Exception {
                return new ContentPropertyAdapter(contentProperty);
            }
        };
    }
}
