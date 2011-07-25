package com.googlecode.utterlyidle.sitemesh;


import com.googlecode.totallylazy.Value;

public class TemplateName implements Value<String> {
    private String templateName;
    public static final TemplateName NONE = templateName("none");

    private TemplateName(String templateName) {
        this.templateName = templateName;
    }

    public static TemplateName templateName(String templateName) {
        return new TemplateName(templateName);
    }

    public String value() {
        return templateName;
    }
}
