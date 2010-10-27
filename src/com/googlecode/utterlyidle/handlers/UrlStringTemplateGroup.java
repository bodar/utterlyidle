package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

import static com.googlecode.utterlyidle.io.Url.url;

public class UrlStringTemplateGroup extends StringTemplateGroup {
    public UrlStringTemplateGroup(Url baseUrl) {
        super(baseUrl.toString(), baseUrl.toString());
    }

    @Override
    protected StringTemplate loadTemplate(String name, String fileName) {
        Url url = url(fileName);
        try {
            return loadTemplate(name, new BufferedReader(url.reader()));
        } catch (IOException e) {
            return null;
        }
    }
}