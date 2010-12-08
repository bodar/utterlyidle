package com.googlecode.utterlyidle.sitemesh;

public class TemplateName {
    private String templateName;

    private TemplateName(String templateName) {
        this.templateName = templateName;
    }

    public static TemplateName templateName(String templateName) {
        return new TemplateName(templateName);
    }

    public String name() {
        return templateName;
    }
}
