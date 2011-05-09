package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.BufferedReader;
import java.io.Reader;

import static com.googlecode.utterlyidle.io.Url.url;

public class UrlStringTemplateGroup extends StringTemplateGroup {
    public UrlStringTemplateGroup(Url baseUrl) {
        super(baseUrl.toString(), baseUrl.toString());
    }

    @Override
    protected StringTemplate loadTemplate(String name, String fileName) {
        try {
            Reader reader = url(fileName).reader();
            StringTemplate template = loadTemplate(name, new BufferedReader(reader));
            reader.close();
            return template;
        } catch (Exception e) {
            return null;
        }
    }
}
