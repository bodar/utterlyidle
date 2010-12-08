package com.googlecode.utterlyidle.sitemesh;

public interface Property {
    boolean hasChild(String name);

    Property getChild(String name);

    String getName();

    String getValue();

    Iterable<Property> getChildren();


}
