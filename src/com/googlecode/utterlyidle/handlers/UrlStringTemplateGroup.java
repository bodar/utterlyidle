package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.BufferedReader;
import java.io.IOException;

import static com.googlecode.utterlyidle.io.Url.url;

public class UrlStringTemplateGroup extends StringTemplateGroup {
    public UrlStringTemplateGroup(Url baseUrl) {
        super(baseUrl.toString(), baseUrl.toString());
        setRefreshInterval(0);
    }

    @Override
    protected StringTemplate loadTemplate(String name, String fileName) {
        try {
            return loadTemplate(name, new BufferedReader(url(fileName).reader()));
        } catch (IOException e) {
            return null;
        }
    }
}
