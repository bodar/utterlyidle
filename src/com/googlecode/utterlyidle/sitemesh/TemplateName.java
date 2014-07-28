package com.googlecode.utterlyidle.sitemesh;


import com.googlecode.totallylazy.Value;

public class TemplateName implements Value<String> {
    private String value;
    public static final TemplateName NONE = templateName("none");

    private TemplateName(String value) {
        this.value = value;
    }

    public static TemplateName templateName(String templateName) {
        return new TemplateName(templateName);
    }

    public String value() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TemplateName && value.equals(((TemplateName) obj).value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
